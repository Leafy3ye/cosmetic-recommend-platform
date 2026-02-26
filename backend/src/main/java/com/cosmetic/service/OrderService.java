package com.cosmetic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmetic.dto.OrderCreateRequest;
import com.cosmetic.entity.Order;

/**
 * 订单Service接口
 */
public interface OrderService {

    /**
     * 创建订单（含明细）
     */
    String createOrder(OrderCreateRequest request);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 支付订单（幂等：记录购买行为 + 累加销量）
     */
    void payOrder(Long orderId, Integer payType);

    /**
     * 发货
     */
    void deliverOrder(Long orderId);

    /**
     * 确认收货
     */
    void confirmOrder(Long orderId);

    /**
     * 根据ID获取订单（含明细）
     */
    Order getOrderById(Long id);

    /**
     * 根据订单号获取订单
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * 分页查询用户订单
     */
    Page<Order> getUserOrderPage(Long userId, Integer current, Integer size, Integer status);

    /**
     * 分页查询商家订单
     */
    Page<Order> getMerchantOrderPage(Long merchantId, Integer current, Integer size, Integer status);
}

