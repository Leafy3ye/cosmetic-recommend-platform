package com.cosmetic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cosmetic.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 商品Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Update("UPDATE tb_product SET sales = sales + #{quantity} WHERE id = #{productId} AND deleted = 0")
    int incrementSales(@Param("productId") Long productId, @Param("quantity") int quantity);
}

