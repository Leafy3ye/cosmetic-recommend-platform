package com.cosmetic.vo;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商家统计数据VO
 */
@Data
public class MerchantStatsVO implements Serializable {

    /**
     * 待发货订单数
     */
    private Integer toShip;

    /**
     * 退款/售后订单数
     */
    private Integer refund;

    /**
     * 在售商品数
     */
    private Integer onSaleProducts;

    /**
     * 已下架商品数
     */
    private Integer offSaleProducts;

    /**
     * 库存预警商品数(库存<10的商品)
     */
    private Integer lowStockProducts;

    /**
     * 今日销售额
     */
    private BigDecimal todaySales;

    /**
     * 今日订单数
     */
    private Integer todayOrders;

    /**
     * 本月销售额
     */
    private BigDecimal monthSales;

    /**
     * 店铺评分
     */
    private Double rating;
}
