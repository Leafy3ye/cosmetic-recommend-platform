package com.cosmetic.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cosmetic.common.Result;
import com.cosmetic.dto.LoginRequest;
import com.cosmetic.dto.LoginResponse;
import com.cosmetic.dto.RegisterRequest;
import com.cosmetic.entity.Order;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.User;
import com.cosmetic.mapper.OrderMapper;
import com.cosmetic.mapper.ProductMapper;
import com.cosmetic.service.UserService;
import com.cosmetic.vo.MerchantStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * 用户Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return Result.success("注册成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                // 隐藏密码
                user.setPassword(null);
                return Result.success(user);
            }
            return Result.error("用户不存在");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<Void> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户地址信息
     */
    @GetMapping("/{userId}/address")
    public Result<User> getUserAddress(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                // 只返回地址相关信息
                User addressInfo = new User();
                addressInfo.setId(user.getId());
                addressInfo.setReceiverName(user.getReceiverName());
                addressInfo.setReceiverPhone(user.getReceiverPhone());
                addressInfo.setProvince(user.getProvince());
                addressInfo.setCity(user.getCity());
                addressInfo.setDistrict(user.getDistrict());
                addressInfo.setDetailAddress(user.getDetailAddress());
                addressInfo.setIsDefault(user.getIsDefault());
                return Result.success(addressInfo);
            }
            return Result.error("用户不存在");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户地址信息
     */
    @PutMapping("/{userId}/address")
    public Result<Void> updateAddress(@PathVariable Long userId, @RequestBody User addressInfo) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 只更新地址相关字段
            user.setReceiverName(addressInfo.getReceiverName());
            user.setReceiverPhone(addressInfo.getReceiverPhone());
            user.setProvince(addressInfo.getProvince());
            user.setCity(addressInfo.getCity());
            user.setDistrict(addressInfo.getDistrict());
            user.setDetailAddress(addressInfo.getDetailAddress());
            user.setIsDefault(addressInfo.getIsDefault());
            
            userService.updateUser(user);
            return Result.success("地址更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传用户头像
     */
    @PutMapping("/{userId}/avatar")
    public Result<Void> uploadAvatar(@PathVariable Long userId, @RequestBody java.util.Map<String, String> request) {
        try {
            String avatar = request.get("avatar");
            if (avatar == null || avatar.isEmpty()) {
                return Result.error("头像数据不能为空");
            }
            
            User user = userService.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 更新用户头像
            user.setAvatar(avatar);
            userService.updateUser(user);
            
            return Result.success("头像上传成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取商家统计数据
     */
    @GetMapping("/merchant/stats/{merchantId}")
    public Result<MerchantStatsVO> getMerchantStats(@PathVariable Long merchantId) {
        try {
            MerchantStatsVO stats = new MerchantStatsVO();
            
            // 1. 统计订单数据
            // 待发货订单数(状态为1)
            Long toShip = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                    .eq(Order::getMerchantId, merchantId)
                    .eq(Order::getStatus, 1)
            );
            stats.setToShip(toShip != null ? toShip.intValue() : 0);
            
            // 退款/售后订单数(状态为5-已取消，暂时用这个代表)
            Long refund = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                    .eq(Order::getMerchantId, merchantId)
                    .eq(Order::getStatus, 5)
            );
            stats.setRefund(refund != null ? refund.intValue() : 0);
            
            // 2. 统计商品数据
            // 在售商品数(status=1)
            Long onSale = productMapper.selectCount(
                new LambdaQueryWrapper<Product>()
                    .eq(Product::getMerchantId, merchantId)
                    .eq(Product::getStatus, 1)
            );
            stats.setOnSaleProducts(onSale != null ? onSale.intValue() : 0);
            
            // 已下架商品数(status=0)
            Long offSale = productMapper.selectCount(
                new LambdaQueryWrapper<Product>()
                    .eq(Product::getMerchantId, merchantId)
                    .eq(Product::getStatus, 0)
            );
            stats.setOffSaleProducts(offSale != null ? offSale.intValue() : 0);
            
            // 库存预警商品数(库存<10的商品)
            Long lowStock = productMapper.selectCount(
                new LambdaQueryWrapper<Product>()
                    .eq(Product::getMerchantId, merchantId)
                    .lt(Product::getStock, 10)
            );
            stats.setLowStockProducts(lowStock != null ? lowStock.intValue() : 0);
            
            // 3. 统计销售数据
            // 今日订单
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = LocalDate.now().plusDays(1).atStartOfDay();
            
            List<Order> todayOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                    .eq(Order::getMerchantId, merchantId)
                    .in(Order::getStatus, 1, 2, 3, 4) // 已支付的订单
                    .ge(Order::getCreateTime, todayStart)
                    .lt(Order::getCreateTime, todayEnd)
            );
            
            stats.setTodayOrders(todayOrders.size());
            
            // 今日销售额
            BigDecimal todaySales = todayOrders.stream()
                .map(Order::getPayAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setTodaySales(todaySales);
            
            // 本月订单
            YearMonth currentMonth = YearMonth.now();
            LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = currentMonth.atEndOfMonth().plusDays(1).atStartOfDay();
            
            List<Order> monthOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                    .eq(Order::getMerchantId, merchantId)
                    .in(Order::getStatus, 1, 2, 3, 4) // 已支付的订单
                    .ge(Order::getCreateTime, monthStart)
                    .lt(Order::getCreateTime, monthEnd)
            );
            
            // 本月销售额
            BigDecimal monthSales = monthOrders.stream()
                .map(Order::getPayAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.setMonthSales(monthSales);
            
            // 店铺评分(暂时固定为4.8，后续可以从评价表中计算)
            stats.setRating(4.8);
            
            return Result.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
}

