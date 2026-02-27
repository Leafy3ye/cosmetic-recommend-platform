package com.cosmetic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmetic.dto.OrderCreateRequest;
import com.cosmetic.entity.Order;
import com.cosmetic.entity.OrderItem;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.User;
import com.cosmetic.exception.BusinessException;
import com.cosmetic.mapper.OrderItemMapper;
import com.cosmetic.mapper.OrderMapper;
import com.cosmetic.mapper.ProductMapper;
import com.cosmetic.mapper.UserMapper;
import com.cosmetic.service.OrderService;
import com.cosmetic.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * 订单Service实现类
 *
 * 订单状态流转：0-待付款 → 1-待发货 → 2-待收货 → 3-待评价 → 4-已完成
 *                              ↘ 5-已取消（仅从0可达）
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserBehaviorService userBehaviorService;

    private static final int BEHAVIOR_TYPE_PURCHASE = 4;

    // ======================== 订单状态常量 ========================
    // 0-待付款, 1-待发货, 2-待收货, 3-待评价, 4-已完成, 5-已取消

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(OrderCreateRequest request) {
        List<OrderCreateRequest.OrderItemDTO> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new BusinessException("订单明细不能为空");
        }
        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 1. 生成订单号 & 构建主订单
        String orderNo = generateOrderNo();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(request.getUserId());
        order.setMerchantId(request.getMerchantId());
        order.setTotalAmount(request.getTotalAmount());
        order.setPayAmount(request.getPayAmount());
        order.setReceiverName(resolveReceiverName(request, user));
        order.setReceiverPhone(resolveReceiverPhone(request, user));
        order.setReceiverAddress(resolveReceiverAddress(request, user));
        order.setRemark(request.getRemark());
        order.setStatus(0); // 待付款

        orderMapper.insert(order);
        Long orderId = order.getId();

        // 2. 逐条写入订单明细
        for (OrderCreateRequest.OrderItemDTO dto : items) {
            Product product = productMapper.selectById(dto.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: productId=" + dto.getProductId());
            }

            // 扣减库存（stock >= quantity 才能成功，原子操作）
            int deducted = productMapper.decrementStock(dto.getProductId(), dto.getQuantity());
            if (deducted == 0) {
                throw new BusinessException("商品 [" + product.getName() + "] 库存不足");
            }

            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setProductId(dto.getProductId());
            item.setProductName(dto.getProductName() != null ? dto.getProductName() : product.getName());
            item.setProductImage(dto.getProductImage() != null ? dto.getProductImage() : product.getImage());
            item.setPrice(dto.getPrice() != null ? dto.getPrice() : product.getPrice());
            item.setQuantity(dto.getQuantity());
            item.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            orderItemMapper.insert(item);
        }

        return orderNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("仅待付款订单可取消");
        }
        order.setStatus(5); // 已取消
        orderMapper.updateById(order);

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());
        }
    }

    /**
     * 支付订单（幂等）
     * 仅当 status==0（待付款）时执行状态变更 + 行为记录 + 销量累加。
     * 已支付/已取消等状态再次调用不会重复执行。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, Integer payType) {
        // 1. 条件更新：仅待付款(status=0)可更新为已支付(status=1)
        int updated = orderMapper.markOrderPaidIfPending(orderId, payType);

        // updated = 0 表示不是待付款（已支付/已取消/不存在）
        if (updated == 0) {
            Order current = orderMapper.selectById(orderId);
            if (current == null) {
                throw new BusinessException("订单不存在");
            }
            if (current.getStatus() == 5) {
                throw new BusinessException("订单已取消，无法支付");
            }
            // 已支付过，幂等返回，不重复执行行为与销量逻辑
            return;
        }

        // 2. 回查最新订单（用于后续行为记录）
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 3. 查询订单明细
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);

        // 4. 为每条明细记录购买行为 + 累加商品销量
        for (OrderItem item : items) {
            userBehaviorService.recordBehavior(
                    order.getUserId(),
                    item.getProductId(),
                    BEHAVIOR_TYPE_PURCHASE
            );

            productMapper.incrementSales(item.getProductId(), item.getQuantity());
        }
    }

    @Override
    public void deliverOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("仅待发货订单可发货");
        }
        order.setStatus(2); // 待收货
        order.setDeliveryTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public void confirmOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("仅待收货订单可确认收货");
        }
        order.setStatus(3); // 待评价
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public Order getOrderById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order != null) {
            order.setItems(orderItemMapper.selectByOrderId(id));
        }
        return order;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);
        if (order != null) {
            order.setItems(orderItemMapper.selectByOrderId(order.getId()));
        }
        return order;
    }

    @Override
    public Page<Order> getUserOrderPage(Long userId, Integer current, Integer size, Integer status) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);

        if (status != null && status >= 0) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        result.getRecords().forEach(o -> o.setItems(orderItemMapper.selectByOrderId(o.getId())));
        return result;
    }

    @Override
    public Page<Order> getMerchantOrderPage(Long merchantId, Integer current, Integer size, Integer status) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getMerchantId, merchantId);

        if (status != null && status >= 0) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        result.getRecords().forEach(o -> o.setItems(orderItemMapper.selectByOrderId(o.getId())));
        return result;
    }

    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String random = String.format("%04d", new Random().nextInt(10000));
        return "CP" + timestamp + random;
    }

    private String resolveReceiverName(OrderCreateRequest request, User user) {
        String name = trimToNull(request.getReceiverName());
        if (isPlaceholderValue(name, "默认收货人")) {
            name = null;
        }
        if (name == null) {
            name = trimToNull(user.getReceiverName());
        }
        if (name == null) {
            name = trimToNull(user.getNickname());
        }
        if (name == null) {
            throw new BusinessException("请先完善收货人信息");
        }
        return name;
    }

    private String resolveReceiverPhone(OrderCreateRequest request, User user) {
        String phone = trimToNull(request.getReceiverPhone());
        if (isPlaceholderValue(phone, "13800000000")) {
            phone = null;
        }
        if (phone == null) {
            phone = trimToNull(user.getReceiverPhone());
        }
        if (phone == null) {
            phone = trimToNull(user.getPhone());
        }
        if (phone == null) {
            throw new BusinessException("请先完善收货人电话");
        }
        return phone;
    }

    private String resolveReceiverAddress(OrderCreateRequest request, User user) {
        String address = trimToNull(request.getReceiverAddress());
        if (isPlaceholderValue(address, "默认收货地址")) {
            address = null;
        }
        if (address == null) {
            address = buildAddressFromUser(user);
        }
        if (address == null) {
            throw new BusinessException("请先在地址管理中完善默认收货地址");
        }
        return address;
    }

    private String buildAddressFromUser(User user) {
        StringBuilder sb = new StringBuilder();
        appendPart(sb, user.getProvince());
        appendPart(sb, user.getCity());
        appendPart(sb, user.getDistrict());
        appendPart(sb, user.getDetailAddress());
        return trimToNull(sb.toString());
    }

    private void appendPart(StringBuilder sb, String part) {
        String value = trimToNull(part);
        if (value == null) {
            return;
        }
        if (sb.length() > 0) {
            sb.append(" ");
        }
        sb.append(value);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isPlaceholderValue(String value, String placeholder) {
        return value != null && value.trim().equals(placeholder);
    }
}
