package com.cosmetic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmetic.common.Result;
import com.cosmetic.entity.Order;
import com.cosmetic.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单Controller
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<String> createOrder(@RequestBody Order order) {
        try {
            String orderNo = orderService.createOrder(order);
            return Result.success("订单创建成功", orderNo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return Result.success("订单已取消", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付订单
     */
    @PutMapping("/pay/{orderId}")
    public Result<Void> payOrder(@PathVariable Long orderId,
                                  @RequestParam Integer payType) {
        try {
            orderService.payOrder(orderId, payType);
            return Result.success("支付成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发货
     */
    @PutMapping("/deliver/{orderId}")
    public Result<Void> deliverOrder(@PathVariable Long orderId) {
        try {
            orderService.deliverOrder(orderId);
            return Result.success("发货成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 确认收货
     */
    @PutMapping("/confirm/{orderId}")
    public Result<Void> confirmOrder(@PathVariable Long orderId) {
        try {
            orderService.confirmOrder(orderId);
            return Result.success("确认收货成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据订单号获取订单
     */
    @GetMapping("/no/{orderNo}")
    public Result<Order> getOrderByNo(@PathVariable String orderNo) {
        try {
            Order order = orderService.getOrderByOrderNo(orderNo);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询用户订单
     */
    @GetMapping("/page/{userId}")
    public Result<Page<Order>> getUserOrderPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        try {
            Page<Order> page = orderService.getUserOrderPage(userId, current, size, status);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询商家订单
     */
    @GetMapping("/merchant/page")
    public Result<Page<Order>> getMerchantOrderPage(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        try {
            Page<Order> page = orderService.getMerchantOrderPage(merchantId, current, size, status);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

