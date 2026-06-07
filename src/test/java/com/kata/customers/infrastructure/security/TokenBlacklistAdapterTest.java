package com.kata.customers.infrastructure.security;

import static org.mockito.Mockito.verify;

import com.kata.customers.security.TokenBlacklistService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistAdapterTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Test
    void revokeShouldDelegateToTokenBlacklistService() {
        TokenBlacklistAdapter adapter = new TokenBlacklistAdapter(tokenBlacklistService);
        Instant expiresAt = Instant.parse("2026-06-07T22:00:00Z");

        adapter.revoke("token-123", expiresAt);

        verify(tokenBlacklistService).revoke("token-123", expiresAt);
    }
}
