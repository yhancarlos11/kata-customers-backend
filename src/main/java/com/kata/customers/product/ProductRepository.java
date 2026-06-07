package com.kata.customers.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCustomerId(Long customerId);

    Optional<Product> findByIdAndCustomerId(Long id, Long customerId);
}
