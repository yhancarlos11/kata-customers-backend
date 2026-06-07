package com.kata.customers.infrastructure.persistence;

import com.kata.customers.application.port.out.RefreshTokenPort;
import com.kata.customers.auth.RefreshToken;
import com.kata.customers.infrastructure.persistence.repository.RefreshTokenRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenJpaAdapter implements RefreshTokenPort {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenJpaAdapter(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }
}
