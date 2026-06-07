package com.kata.customers.infrastructure.security;

import com.kata.customers.application.port.out.PasswordHashPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHashAdapter implements PasswordHashPort {

    private final PasswordEncoder passwordEncoder;

    public PasswordHashAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
