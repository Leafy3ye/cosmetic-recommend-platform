package com.cosmetic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cosmetic.entity.UserBehavior;
import com.cosmetic.mapper.UserBehaviorMapper;
import com.cosmetic.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户行为Service实现类
 */
@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {

    @Autowired
    private UserBehaviorMapper userBehaviorMapper;

    @Override
    public void recordBehavior(Long userId, Long productId, Integer behaviorType) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setBehaviorType(behaviorType);
        
        // 设置行为权重：浏览1分，收藏2分，加购物车3分，购买5分
        int weight = getWeightByType(behaviorType);
        behavior.setWeight(weight);
        
        userBehaviorMapper.insert(behavior);
    }

    @Override
    public List<UserBehavior> getUserBehaviors(Long userId) {
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getUserId, userId);
        wrapper.orderByDesc(UserBehavior::getCreateTime);
        return userBehaviorMapper.selectList(wrapper);
    }

    @Override
    public List<UserBehavior> getProductBehaviors(Long productId) {
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getProductId, productId);
        wrapper.orderByDesc(UserBehavior::getCreateTime);
        return userBehaviorMapper.selectList(wrapper);
    }

    @Override
    public List<UserBehavior> getBehaviorsByType(Long userId, Integer behaviorType) {
        LambdaQueryWrapper<UserBehavior> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBehavior::getUserId, userId);
        wrapper.eq(UserBehavior::getBehaviorType, behaviorType);
        wrapper.orderByDesc(UserBehavior::getCreateTime);
        return userBehaviorMapper.selectList(wrapper);
    }

    /**
     * 根据行为类型获取权重
     */
    private int getWeightByType(Integer behaviorType) {
        switch (behaviorType) {
            case 1: return 1;  // 浏览
            case 2: return 2;  // 收藏
            case 3: return 3;  // 加购物车
            case 4: return 5;  // 购买
            default: return 1;
        }
    }
}

