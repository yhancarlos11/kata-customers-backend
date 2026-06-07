package com.kata.customers.application.port.in;

import com.kata.customers.product.CreateProductRequest;
import com.kata.customers.product.ProductResponse;
import java.util.List;

public interface ProductUseCase {

    ProductResponse create(Long customerId, CreateProductRequest request);

    List<ProductResponse> listByCustomer(Long customerId);

    ProductResponse update(Long customerId, Long productId, CreateProductRequest request);

    void delete(Long customerId, Long productId);
}
