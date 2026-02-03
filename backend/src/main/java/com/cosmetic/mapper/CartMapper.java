package com.cosmetic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cosmetic.entity.Cart;
import com.cosmetic.vo.CartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物车Mapper
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 获取用户购物车列表（包含商品信息）
     */
    @Select("SELECT c.id, c.user_id as userId, c.product_id as productId, " +
            "p.name as productName, p.image as productImage, p.brand as productBrand, " +
            "p.price, c.quantity, c.checked, c.create_time as createTime " +
            "FROM tb_cart c " +
            "LEFT JOIN tb_product p ON c.product_id = p.id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.create_time DESC")
    List<CartVO> getCartListWithProduct(Long userId);
}

