package com.kata.customers.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kata.customers.security.JwtService;
import com.kata.customers.user.AppUser;
import com.kata.customers.user.UserRole;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class JwtTokenAdapterTest {

    @Mock
    private JwtService jwtService;

    @Test
    void generateTokenShouldBuildUserDetailsAndDelegateToJwtService() {
        JwtTokenAdapter adapter = new JwtTokenAdapter(jwtService);
        AppUser user = new AppUser(1L, "demo", "demo@correo.com", "hashed", UserRole.USER);

        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        String token = adapter.generateToken(user);

        assertEquals("jwt-token", token);
        verify(jwtService).generateToken(any(UserDetails.class));
    }

    @Test
    void extractExpirationShouldConvertDateToInstant() {
        JwtTokenAdapter adapter = new JwtTokenAdapter(jwtService);
        Instant expected = Instant.parse("2026-06-07T22:00:00Z");
        when(jwtService.extractExpiration("jwt-token")).thenReturn(Date.from(expected));

        Instant result = adapter.extractExpiration("jwt-token");

        assertNotNull(result);
        assertEquals(expected, result);
        verify(jwtService).extractExpiration("jwt-token");
    }
}
