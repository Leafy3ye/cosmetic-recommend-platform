package com.cosmetic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cosmetic.entity.Favorite;
import com.cosmetic.entity.Product;
import com.cosmetic.mapper.FavoriteMapper;
import com.cosmetic.mapper.ProductMapper;
import com.cosmetic.service.FavoriteService;
import com.cosmetic.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏Service实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserBehaviorService userBehaviorService;

    @Override
    public void addFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId);
        Favorite existed = favoriteMapper.selectOne(wrapper);
        if (existed != null) {
            return;
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);

        // 收藏行为埋点
        userBehaviorService.recordBehavior(userId, productId, 2);
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getProductId, productId);
        favoriteMapper.delete(wrapper);
    }

    @Override
    public List<Product> getFavoriteProducts(Long userId, Integer limit) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime);
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        List<Product> products = new ArrayList<>();
        for (Favorite favorite : favorites) {
            Product product = productMapper.selectById(favorite.getProductId());
            if (product != null && product.getStatus() != null && product.getStatus() == 1) {
                products.add(product);
            }
        }
        return products;
    }
}

