package com.kata.customers.application.port.out;

import com.kata.customers.product.Product;
import java.util.List;
import java.util.Optional;

public interface ProductPort {

    Product save(Product product);

    List<Product> findByCustomerId(Long customerId);

    Optional<Product> findByIdAndCustomerId(Long productId, Long customerId);

    void delete(Product product);
}
