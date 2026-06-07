package com.kata.customers.application.port.in;

import com.kata.customers.customer.CreateCustomerRequest;
import com.kata.customers.customer.CustomerDetailResponse;
import com.kata.customers.customer.CustomerResponse;
import java.util.List;

public interface CustomerUseCase {

    CustomerResponse create(CreateCustomerRequest request);

    List<CustomerResponse> findAll();

    CustomerDetailResponse findById(Long customerId);

    CustomerResponse update(Long customerId, CreateCustomerRequest request);

    void delete(Long customerId);
}
