package com.kata.customers.infrastructure.security;

import com.kata.customers.application.port.out.TokenPort;
import com.kata.customers.security.JwtService;
import com.kata.customers.user.AppUser;
import java.time.Instant;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenAdapter implements TokenPort {

    private final JwtService jwtService;

    public JwtTokenAdapter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateToken(AppUser user) {
        UserDetails userDetails = User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();

        return jwtService.generateToken(userDetails);
    }

    @Override
    public Instant extractExpiration(String token) {
        return jwtService.extractExpiration(token).toInstant();
    }
}
