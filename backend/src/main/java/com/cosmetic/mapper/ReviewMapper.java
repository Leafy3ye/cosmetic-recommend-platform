package com.cosmetic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cosmetic.entity.Review;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论Mapper
 */
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
}

