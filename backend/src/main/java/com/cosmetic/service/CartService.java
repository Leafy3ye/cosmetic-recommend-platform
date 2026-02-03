package com.cosmetic.service;

import com.cosmetic.entity.Cart;
import com.cosmetic.vo.CartVO;
import java.util.List;

/**
 * 购物车Service接口
 */
public interface CartService {

    /**
     * 添加到购物车
     */
    void addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    void updateCartQuantity(Long cartId, Integer quantity);

    /**
     * 删除购物车商品
     */
    void deleteCartItem(Long cartId);

    /**
     * 清空购物车
     */
    void clearCart(Long userId);

    /**
     * 获取用户购物车列表
     */
    List<Cart> getCartList(Long userId);

    /**
     * 获取用户购物车列表（包含商品信息）
     */
    List<CartVO> getCartListWithProduct(Long userId);

    /**
     * 切换选中状态
     */
    void toggleCheck(Long cartId, Integer checked);
}

