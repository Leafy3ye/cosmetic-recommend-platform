package com.cosmetic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cosmetic.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 条件更新订单为已支付状态（仅待付款状态可更新）
     */
    @Update("UPDATE tb_order SET status = 1, pay_type = #{payType}, pay_time = NOW() " +
            "WHERE id = #{orderId} AND status = 0 AND deleted = 0")
    int markOrderPaidIfPending(@Param("orderId") Long orderId, @Param("payType") Integer payType);
}

