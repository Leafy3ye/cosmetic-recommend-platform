package com.cosmetic.controller;

import com.cosmetic.common.Result;
import com.cosmetic.entity.Product;
import com.cosmetic.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 推荐算法Controller
 * 提供个性化推荐接口
 */
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取用户个性化推荐商品
     */
    @GetMapping("/user/{userId}")
    public Result<List<Product>> getRecommendProducts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Product> products = recommendService.getRecommendProducts(userId, limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 基于用户协同过滤的推荐
     */
    @GetMapping("/user-cf/{userId}")
    public Result<List<Product>> getUserBasedRecommend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Product> products = recommendService.getUserBasedRecommend(userId, limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 基于物品协同过滤的推荐
     */
    @GetMapping("/item-cf/{userId}")
    public Result<List<Product>> getItemBasedRecommend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Product> products = recommendService.getItemBasedRecommend(userId, limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取相似商品
     */
    @GetMapping("/similar/product/{productId}")
    public Result<List<Long>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Long> products = recommendService.getSimilarProducts(productId, limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 手动触发推荐模型更新
     */
    @PostMapping("/update-model")
    public Result<Void> updateRecommendModel() {
        try {
            recommendService.updateRecommendModel();
            return Result.success("推荐模型更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

