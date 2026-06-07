package com.kata.customers.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kata.customers.customer.Customer;
import com.kata.customers.infrastructure.persistence.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerJpaAdapterTest {

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void shouldDelegateCrudAndLookupOperations() {
        CustomerJpaAdapter adapter = new CustomerJpaAdapter(customerRepository);
        Customer customer = new Customer(1L, "Ana", "ana@correo.com", LocalDateTime.now());

        when(customerRepository.existsByEmail("ana@correo.com")).thenReturn(true);
        when(customerRepository.existsByEmailAndIdNot("ana@correo.com", 1L)).thenReturn(false);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertTrue(adapter.existsByEmail("ana@correo.com"));
        assertEquals(false, adapter.existsByEmailAndIdNot("ana@correo.com", 1L));
        assertSame(customer, adapter.save(customer));
        assertEquals(1, adapter.findAll().size());
        assertTrue(adapter.findById(1L).isPresent());

        adapter.delete(customer);

        verify(customerRepository).existsByEmail("ana@correo.com");
        verify(customerRepository).existsByEmailAndIdNot("ana@correo.com", 1L);
        verify(customerRepository).save(customer);
        verify(customerRepository).findAll();
        verify(customerRepository).findById(1L);
        verify(customerRepository).delete(customer);
    }
}
