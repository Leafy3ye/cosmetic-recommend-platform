package com.cosmetic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cosmetic.common.Result;
import com.cosmetic.entity.Product;
import com.cosmetic.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品Controller
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 添加商品
     */
    @PostMapping("/add")
    public Result<Void> addProduct(@RequestBody Product product) {
        try {
            productService.addProduct(product);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新商品
     */
    @PutMapping("/update")
    public Result<Void> updateProduct(@RequestBody Product product) {
        try {
            productService.updateProduct(product);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return Result.success(product);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询商品
     */
    @GetMapping("/page")
    public Result<Page<Product>> getProductPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String type) {
        try {
            Page<Product> page = productService.getProductPage(current, size, keyword, categoryId, type);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取热销商品
     */
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Product> products = productService.getHotProducts(limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取推荐商品
     */
    @GetMapping("/recommend/{userId}")
    public Result<List<Product>> getRecommendProducts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Product> products = productService.getRecommendProducts(userId, limit);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取商家的商品列表（分页）
     */
    @GetMapping("/merchant/page")
    public Result<Page<Product>> getMerchantProductPage(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        try {
            Page<Product> page = productService.getMerchantProductPage(merchantId, current, size, keyword, status);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上架/下架商品
     */
    @PutMapping("/status/{id}")
    public Result<Void> updateProductStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            productService.updateProductStatus(id, status);
            return Result.success("操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

