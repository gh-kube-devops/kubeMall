package com.kubemall.product.service;

import com.kubemall.product.entity.Product;
import com.kubemall.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Override
    public Product create(Product product) {
        return repository.save(product);
    }

    @Override
    public Product getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}