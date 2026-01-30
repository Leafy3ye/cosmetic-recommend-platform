package com.cosmetic.service;

import com.cosmetic.entity.UserBehavior;
import java.util.List;

/**
 * 用户行为Service接口
 */
public interface UserBehaviorService {

    /**
     * 记录用户行为
     * 
     * @param userId 用户ID
     * @param productId 商品ID
     * @param behaviorType 行为类型：1-浏览，2-收藏，3-加购物车，4-购买
     */
    void recordBehavior(Long userId, Long productId, Integer behaviorType);

    /**
     * 获取用户行为记录
     */
    List<UserBehavior> getUserBehaviors(Long userId);

    /**
     * 获取商品的用户行为记录
     */
    List<UserBehavior> getProductBehaviors(Long productId);

    /**
     * 获取指定行为类型的记录
     */
    List<UserBehavior> getBehaviorsByType(Long userId, Integer behaviorType);
}

