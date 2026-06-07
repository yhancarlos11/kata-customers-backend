package com.kata.customers.infrastructure.security;

import com.kata.customers.application.port.out.TokenRevocationPort;
import com.kata.customers.security.TokenBlacklistService;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class TokenBlacklistAdapter implements TokenRevocationPort {

    private final TokenBlacklistService tokenBlacklistService;

    public TokenBlacklistAdapter(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public void revoke(String token, Instant expiresAt) {
        tokenBlacklistService.revoke(token, expiresAt);
    }
}
