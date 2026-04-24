package com.kubemall.product.dto.response;

import com.kubemall.product.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;

    public static ProductResponse from(Product product) {
        ProductResponse res = new ProductResponse();
        res.setId(product.getId());
        res.setName(product.getName());
        res.setPrice(product.getPrice());
        res.setStock(product.getStock());
        res.setDescription(product.getDescription());
        return res;
    }
}