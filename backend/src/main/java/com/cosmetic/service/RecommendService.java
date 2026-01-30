package com.cosmetic.service;

import com.cosmetic.entity.Product;
import java.util.List;

/**
 * 推荐算法Service接口
 * 预留接口，后续实现UserCF和ItemCF协同过滤算法
 */
public interface RecommendService {

    /**
     * 获取用户推荐商品列表
     * 基于用户协同过滤（UserCF）和物品协同过滤（ItemCF）
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<Product> getRecommendProducts(Long userId, Integer limit);

    /**
     * 基于用户协同过滤推荐
     * 找到相似用户，推荐相似用户喜欢的商品
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<Product> getUserBasedRecommend(Long userId, Integer limit);

    /**
     * 基于物品协同过滤推荐
     * 根据用户历史行为，推荐相似的商品
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    List<Product> getItemBasedRecommend(Long userId, Integer limit);

    /**
     * 计算用户相似度
     * 基于用户行为（浏览、收藏、购买、评分）
     * 
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @return 相似度（0-1之间）
     */
    Double calculateUserSimilarity(Long userId1, Long userId2);

    /**
     * 计算商品相似度
     * 基于商品属性和用户行为
     * 
     * @param productId1 商品1 ID
     * @param productId2 商品2 ID
     * @return 相似度（0-1之间）
     */
    Double calculateProductSimilarity(Long productId1, Long productId2);

    /**
     * 获取相似用户列表
     * 
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 相似用户ID列表
     */
    List<Long> getSimilarUsers(Long userId, Integer limit);

    /**
     * 获取相似商品列表
     * 
     * @param productId 商品ID
     * @param limit 数量限制
     * @return 相似商品ID列表
     */
    List<Long> getSimilarProducts(Long productId, Integer limit);

    /**
     * 更新推荐模型
     * 定期执行，基于最新的用户行为数据更新推荐模型
     */
    void updateRecommendModel();
}

