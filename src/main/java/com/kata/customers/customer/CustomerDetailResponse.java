package com.kata.customers.customer;

import com.kata.customers.product.ProductResponse;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerDetailResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private List<ProductResponse> products;

    public CustomerDetailResponse(
        Long id,
        String name,
        String email,
        LocalDateTime createdAt,
        List<ProductResponse> products
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }
}
