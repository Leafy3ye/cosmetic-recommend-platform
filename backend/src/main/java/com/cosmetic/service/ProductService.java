package com.cosmetic.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmetic.entity.Product;

import java.util.List;

/**
 * 商品Service接口
 */
public interface ProductService {

    /**
     * 添加商品
     */
    void addProduct(Product product);

    /**
     * 更新商品
     */
    void updateProduct(Product product);

    /**
     * 删除商品
     */
    void deleteProduct(Long id);

    /**
     * 根据ID获取商品
     */
    Product getProductById(Long id);

    /**
     * 分页查询商品
     */
    Page<Product> getProductPage(Integer current, Integer size, String keyword, Long categoryId);

    /**
     * 获取热销商品
     */
    List<Product> getHotProducts(Integer limit);

    /**
     * 获取推荐商品（预留推荐算法接口）
     */
    List<Product> getRecommendProducts(Long userId, Integer limit);

    /**
     * 获取商家的商品列表（分页）
     */
    Page<Product> getMerchantProductPage(Long merchantId, Integer current, Integer size, String keyword, Integer status);

    /**
     * 更新商品状态
     */
    void updateProductStatus(Long id, Integer status);
}

