package com.cosmetic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmetic.entity.Product;
import com.cosmetic.mapper.ProductMapper;
import com.cosmetic.service.ProductService;
import com.cosmetic.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品Service实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired(required = false)
    private RecommendService recommendService;

    @Override
    public void addProduct(Product product) {
        product.setSales(0);
        productMapper.insert(product);
    }

    @Override
    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public Page<Product> getProductPage(Integer current, Integer size, String keyword, Long categoryId, String type) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询上架的商品
        wrapper.eq(Product::getStatus, 1);
        
        // 热卖：仅展示销量 > 0 的商品
        if ("hot".equals(type)) {
            wrapper.gt(Product::getSales, 0);
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getDescription, keyword)
                    .or().like(Product::getBrand, keyword));
        }
        
        // 分类筛选
        if (categoryId != null && categoryId > 0) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        // 排序：热卖按销量降序，新品按上架时间降序（最新在前）
        if ("hot".equals(type)) {
            wrapper.orderByDesc(Product::getSales);
        } else if ("new".equals(type)) {
            wrapper.orderByDesc(Product::getCreateTime);
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }
        
        return productMapper.selectPage(page, wrapper);
    }

    @Override
    public List<Product> getHotProducts(Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.orderByDesc(Product::getSales);
        wrapper.last("LIMIT " + limit);
        return productMapper.selectList(wrapper);
    }

    @Override
    public List<Product> getRecommendProducts(Long userId, Integer limit) {
        // 如果推荐服务可用，使用推荐算法
        if (recommendService != null) {
            return recommendService.getRecommendProducts(userId, limit);
        }
        
        // 否则返回热销商品
        return getHotProducts(limit);
    }

    @Override
    public Page<Product> getMerchantProductPage(Long merchantId, Integer current, Integer size, String keyword, Integer status) {
        Page<Product> page = new Page<>(current, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        // 筛选商家的商品
        wrapper.eq(Product::getMerchantId, merchantId);
        
        // 状态筛选
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        
        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getDescription, keyword)
                    .or().like(Product::getBrand, keyword));
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(page, wrapper);
    }

    @Override
    public void updateProductStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        productMapper.updateById(product);
    }
}

