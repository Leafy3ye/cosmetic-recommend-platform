package com.cosmetic.controller;

import com.cosmetic.common.Result;
import com.cosmetic.entity.Cart;
import com.cosmetic.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车Controller
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加到购物车
     */
    @PostMapping("/add")
    public Result<Void> addToCart(@RequestParam Long userId,
                                   @RequestParam Long productId,
                                   @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            cartService.addToCart(userId, productId, quantity);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public Result<Void> updateQuantity(@RequestParam Long cartId,
                                        @RequestParam Integer quantity) {
        try {
            cartService.updateCartQuantity(cartId, quantity);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{cartId}")
    public Result<Void> deleteCartItem(@PathVariable Long cartId) {
        try {
            cartService.deleteCartItem(cartId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear/{userId}")
    public Result<Void> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return Result.success("清空成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户购物车列表
     */
    @GetMapping("/list/{userId}")
    public Result<List<Cart>> getCartList(@PathVariable Long userId) {
        try {
            List<Cart> carts = cartService.getCartList(userId);
            return Result.success(carts);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 切换选中状态
     */
    @PutMapping("/check")
    public Result<Void> toggleCheck(@RequestParam Long cartId,
                                     @RequestParam Integer checked) {
        try {
            cartService.toggleCheck(cartId, checked);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

