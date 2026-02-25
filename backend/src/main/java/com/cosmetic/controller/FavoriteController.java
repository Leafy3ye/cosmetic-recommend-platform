package com.cosmetic.controller;

import com.cosmetic.common.Result;
import com.cosmetic.entity.Product;
import com.cosmetic.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏Controller
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<Void> addFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            favoriteService.addFavorite(userId, productId);
            return Result.success("收藏成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove")
    public Result<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            favoriteService.removeFavorite(userId, productId);
            return Result.success("已取消收藏", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户收藏商品
     */
    @GetMapping("/user/{userId}")
    public Result<List<Product>> getFavoriteProducts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<Product> products = favoriteService.getFavoriteProducts(userId, limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

