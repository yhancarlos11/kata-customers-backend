package com.kata.customers.customer;

import java.time.LocalDateTime;

public class CustomerResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public CustomerResponse(Long id, String name, String email, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
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
}
