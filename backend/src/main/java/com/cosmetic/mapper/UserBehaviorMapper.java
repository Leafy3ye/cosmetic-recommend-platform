package com.cosmetic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cosmetic.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户行为Mapper
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
}

