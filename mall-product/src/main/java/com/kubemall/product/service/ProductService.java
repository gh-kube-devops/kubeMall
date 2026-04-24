package com.kubemall.product.service;

import com.kubemall.product.entity.Product;

/**
 * 用户服务接口
 */
public interface ProductService {

    Product create(Product product);

    Product getById(Long id);

    void delete(Long id);

}