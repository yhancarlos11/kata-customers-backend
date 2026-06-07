package com.kata.customers.customer;

import com.kata.customers.application.port.out.CustomerPort;
import com.kata.customers.common.ResourceNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerPort customerPort;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerPort);
    }

    @Test
    void createShouldPersistCustomerWhenEmailIsAvailable() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Juan Perez");
        request.setEmail("juan@email.com");

        Customer savedCustomer = new Customer(
            1L,
            "Juan Perez",
            "juan@email.com",
            LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        when(customerPort.existsByEmail("juan@email.com")).thenReturn(false);
        when(customerPort.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponse result = customerService.create(request);

        assertEquals(1L, result.getId());
        assertEquals("Juan Perez", result.getName());
        assertEquals("juan@email.com", result.getEmail());
        verify(customerPort).save(any(Customer.class));
    }

    @Test
    void createShouldFailWhenEmailAlreadyExists() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Juan Perez");
        request.setEmail("juan@email.com");

        when(customerPort.existsByEmail("juan@email.com")).thenReturn(true);

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> customerService.create(request)
        );

        assertTrue(error.getMessage().contains("email"));
        verify(customerPort, never()).save(any(Customer.class));
    }

    @Test
    void findAllShouldMapRepositoryEntitiesToResponses() {
        when(customerPort.findAll()).thenReturn(
            List.of(
                new Customer(1L, "Ana", "ana@email.com", LocalDateTime.of(2026, 1, 1, 8, 0)),
                new Customer(2L, "Luis", "luis@email.com", LocalDateTime.of(2026, 1, 1, 9, 0))
            )
        );

        List<CustomerResponse> result = customerService.findAll();

        assertEquals(2, result.size());
        assertEquals("Ana", result.get(0).getName());
        assertEquals("luis@email.com", result.get(1).getEmail());
    }

    @Test
    void findByIdShouldReturnCustomerWhenExists() {
        Customer customer = new Customer(3L, "Maria", "maria@email.com", LocalDateTime.of(2026, 1, 2, 10, 0));
        when(customerPort.findById(3L)).thenReturn(Optional.of(customer));

        CustomerDetailResponse result = customerService.findById(3L);

        assertEquals(3L, result.getId());
        assertEquals("Maria", result.getName());
        assertEquals("maria@email.com", result.getEmail());
    }

    @Test
    void updateShouldPersistNewValuesWhenCustomerExists() {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("Cliente Actualizado");
        request.setEmail("nuevo@email.com");

        Customer existing = new Customer(
            2L,
            "Cliente Viejo",
            "viejo@email.com",
            LocalDateTime.of(2026, 1, 1, 12, 0)
        );

        when(customerPort.findById(2L)).thenReturn(Optional.of(existing));
        when(customerPort.existsByEmailAndIdNot("nuevo@email.com", 2L)).thenReturn(false);
        when(customerPort.save(existing)).thenReturn(existing);

        CustomerResponse result = customerService.update(2L, request);

        assertEquals("Cliente Actualizado", result.getName());
        assertEquals("nuevo@email.com", result.getEmail());
        verify(customerPort).save(existing);
    }

    @Test
    void deleteShouldFailWhenCustomerDoesNotExist() {
        when(customerPort.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> customerService.delete(99L));
        verify(customerPort, never()).delete(any(Customer.class));
    }
}
