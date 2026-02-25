package com.cosmetic.service;

import com.cosmetic.entity.Product;

import java.util.List;

/**
 * 收藏Service接口
 */
public interface FavoriteService {

    /**
     * 添加收藏
     */
    void addFavorite(Long userId, Long productId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long productId);

    /**
     * 获取用户收藏商品列表
     */
    List<Product> getFavoriteProducts(Long userId, Integer limit);
}

