package com.kata.customers.application.port.out;

import com.kata.customers.auth.RefreshToken;
import java.util.Optional;

public interface RefreshTokenPort {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken save(RefreshToken refreshToken);
}
