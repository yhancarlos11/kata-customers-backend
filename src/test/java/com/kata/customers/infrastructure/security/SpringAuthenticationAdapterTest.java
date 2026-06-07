package com.kata.customers.infrastructure.security;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class SpringAuthenticationAdapterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Test
    void authenticateShouldDelegateToAuthenticationManager() {
        SpringAuthenticationAdapter adapter = new SpringAuthenticationAdapter(authenticationManager);

        adapter.authenticate("demoUser", "Secret123*");

        verify(authenticationManager)
            .authenticate(new UsernamePasswordAuthenticationToken("demoUser", "Secret123*"));
    }
}
