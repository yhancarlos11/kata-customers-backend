package com.kata.customers.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordHashAdapterTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void encodeShouldDelegateToPasswordEncoder() {
        PasswordHashAdapter adapter = new PasswordHashAdapter(passwordEncoder);
        when(passwordEncoder.encode("Secret123*")).thenReturn("$2a$10$hash");

        String result = adapter.encode("Secret123*");

        assertEquals("$2a$10$hash", result);
        verify(passwordEncoder).encode("Secret123*");
    }
}
