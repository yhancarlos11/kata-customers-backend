package com.kata.customers.customer;

import com.kata.customers.application.port.in.CustomerUseCase;
import com.kata.customers.application.port.out.CustomerPort;
import com.kata.customers.common.ResourceNotFoundException;
import com.kata.customers.product.ProductResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService implements CustomerUseCase {

    private final CustomerPort customerPort;

    public CustomerService(CustomerPort customerPort) {
        this.customerPort = customerPort;
    }

    @Override
    public CustomerResponse create(CreateCustomerRequest request) {
        if (customerPort.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Ya existe un customer con este email");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());

        Customer saved = customerPort.save(customer);
        return toResponse(saved);
    }

    @Override
    public List<CustomerResponse> findAll() {
        return customerPort.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDetailResponse findById(Long customerId) {
        Customer customer = customerPort
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

    @Override
    public CustomerResponse update(Long customerId, CreateCustomerRequest request) {
        Customer customer = customerPort
            .findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (customerPort.existsByEmailAndIdNot(request.getEmail(), customerId)) {
            throw new IllegalArgumentException("Ya existe un customer con este email");
        }

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());

        Customer saved = customerPort.save(customer);
        return toResponse(saved);
    }

    @Override
    public void delete(Long customerId) {
        Customer customer = customerPort
            .findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        customerPort.delete(customer);
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
