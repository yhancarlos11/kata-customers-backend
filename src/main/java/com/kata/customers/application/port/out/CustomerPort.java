package com.kata.customers.application.port.out;

import com.kata.customers.customer.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerPort {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Customer save(Customer customer);

    List<Customer> findAll();

    Optional<Customer> findById(Long customerId);

    void delete(Customer customer);
}
