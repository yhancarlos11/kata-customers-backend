package com.kata.customers.infrastructure.persistence;

import com.kata.customers.application.port.out.CustomerPort;
import com.kata.customers.customer.Customer;
import com.kata.customers.infrastructure.persistence.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CustomerJpaAdapter implements CustomerPort {

    private final CustomerRepository customerRepository;

    public CustomerJpaAdapter(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return customerRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public void delete(Customer customer) {
        customerRepository.delete(customer);
    }
}
