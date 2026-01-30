package com.cosmetic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmetic.entity.Order;
import com.cosmetic.mapper.OrderMapper;
import com.cosmetic.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单Service实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public String createOrder(Order order) {
        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(0);  // 待付款
        
        orderMapper.insert(order);
        return orderNo;
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && order.getStatus() == 0) {
            order.setStatus(5);  // 已取消
            orderMapper.updateById(order);
        }
    }

    @Override
    public void payOrder(Long orderId, Integer payType) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && order.getStatus() == 0) {
            order.setStatus(1);  // 待发货
            order.setPayType(payType);
            order.setPayTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    @Override
    public void deliverOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && order.getStatus() == 1) {
            order.setStatus(2);  // 待收货
            order.setDeliveryTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    @Override
    public void confirmOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order != null && order.getStatus() == 2) {
            order.setStatus(3);  // 待评价
            order.setFinishTime(LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }

    @Override
    public Order getOrderById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return orderMapper.selectOne(wrapper);
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
        return orderMapper.selectPage(page, wrapper);
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
        return orderMapper.selectPage(page, wrapper);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String random = String.format("%04d", new Random().nextInt(10000));
        return "CP" + timestamp + random;
    }
}

