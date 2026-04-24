package com.kubemall.product.controller;

import com.kubemall.product.common.Result;
import com.kubemall.product.dto.request.ProductCreateRequest;
import com.kubemall.product.dto.response.ProductResponse;
import com.kubemall.product.entity.Product;
import com.kubemall.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * POST /products - 创建商品
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());

        Product saved = productService.create(product);
        return Result.success(ProductResponse.from(saved));
    }

    /**
     * GET /products/{id} - 获取商品
     */
    @GetMapping("/{id}")
    public Result<ProductResponse> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(ProductResponse.from(product));
    }

    /**
     * DELETE /products/{id} - 删除商品
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteById(@PathVariable Long id) {
        productService.delete(id);
        return Result.success("Product deleted successfully", null);
    }
}