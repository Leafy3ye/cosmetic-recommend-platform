package com.cosmetic.controller;

import com.cosmetic.common.Result;
import com.cosmetic.entity.Product;
import com.cosmetic.entity.UserBehavior;
import com.cosmetic.mapper.ProductMapper;
import com.cosmetic.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户行为Controller
 */
@RestController
@RequestMapping("/behavior")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 记录用户行为
     */
    @PostMapping("/record")
    public Result<Void> recordBehavior(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer behaviorType) {
        try {
            userBehaviorService.recordBehavior(userId, productId, behaviorType);
            return Result.success("记录成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户行为记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<UserBehavior>> getUserBehaviors(@PathVariable Long userId) {
        try {
            List<UserBehavior> behaviors = userBehaviorService.getUserBehaviors(userId);
            return Result.success(behaviors);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取商品的用户行为记录
     */
    @GetMapping("/product/{productId}")
    public Result<List<UserBehavior>> getProductBehaviors(@PathVariable Long productId) {
        try {
            List<UserBehavior> behaviors = userBehaviorService.getProductBehaviors(productId);
            return Result.success(behaviors);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户足迹商品（按最近浏览时间倒序，去重）
     */
    @GetMapping("/history/{userId}")
    public Result<List<Product>> getHistoryProducts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<UserBehavior> behaviors = userBehaviorService.getBehaviorsByType(userId, 1);
            List<Long> productIds = behaviors.stream()
                    .map(UserBehavior::getProductId)
                    .distinct()
                    .limit(limit)
                    .collect(Collectors.toList());

            List<Product> products = new ArrayList<>();
            for (Long productId : productIds) {
                Product product = productMapper.selectById(productId);
                if (product != null && product.getStatus() != null && product.getStatus() == 1) {
                    products.add(product);
                }
            }
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

