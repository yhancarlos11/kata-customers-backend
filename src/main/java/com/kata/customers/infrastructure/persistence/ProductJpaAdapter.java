package com.kata.customers.infrastructure.persistence;

import com.kata.customers.application.port.out.ProductPort;
import com.kata.customers.infrastructure.persistence.repository.ProductRepository;
import com.kata.customers.product.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ProductJpaAdapter implements ProductPort {

    private final ProductRepository productRepository;

    public ProductJpaAdapter(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> findByCustomerId(Long customerId) {
        return productRepository.findByCustomerId(customerId);
    }

    @Override
    public Optional<Product> findByIdAndCustomerId(Long productId, Long customerId) {
        return productRepository.findByIdAndCustomerId(productId, customerId);
    }

    @Override
    public void delete(Product product) {
        productRepository.delete(product);
    }
}
