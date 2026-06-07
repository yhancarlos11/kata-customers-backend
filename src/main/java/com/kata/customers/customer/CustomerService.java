package com.kata.customers.customer;

import com.kata.customers.common.ResourceNotFoundException;
import com.kata.customers.product.ProductResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public CustomerDetailResponse findById(Long customerId) {
        Customer customer = customerRepository
            .findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        List<ProductResponse> products = customer
            .getProducts()
            .stream()
            .map(product ->
                new ProductResponse(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getDescription()
                )
            )
            .toList();

        return new CustomerDetailResponse(
            customer.getId(),
            customer.getName(),
            customer.getEmail(),
            customer.getCreatedAt(),
            products
        );
    }

    public CustomerResponse update(Long customerId, CreateCustomerRequest request) {
        Customer customer = customerRepository
            .findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (customerRepository.existsByEmailAndIdNot(request.getEmail(), customerId)) {
            throw new IllegalArgumentException("Ya existe un customer con este email");
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());

        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public void delete(Long customerId) {
        Customer customer = customerRepository
            .findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        customerRepository.delete(customer);
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
