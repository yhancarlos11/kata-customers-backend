package com.kata.customers.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(String token, Instant expiresAt) {
        cleanupExpiredEntries();
        revokedTokens.put(token, expiresAt);
    }

    public boolean isRevoked(String token) {
        cleanupExpiredEntries();
        return revokedTokens.containsKey(token);
    }

    private void cleanupExpiredEntries() {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
