package com.kata.customers.application.port.out;

import java.time.Instant;

public interface TokenRevocationPort {

    void revoke(String token, Instant expiresAt);
}
