package com.kata.customers.customer;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse create(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un customer con este email");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream().map(this::toResponse).toList();
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getCreatedAt()
        );
    }
}
