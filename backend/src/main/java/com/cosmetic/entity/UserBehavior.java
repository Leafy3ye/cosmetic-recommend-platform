package com.cosmetic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户行为记录实体类（用于推荐算法）
 */
@Data
@TableName("tb_user_behavior")
public class UserBehavior implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 行为类型：1-浏览，2-收藏，3-加购物车，4-购买
     */
    private Integer behaviorType;

    /**
     * 行为权重（用于推荐计算）
     */
    private Integer weight;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

