package com.kata.customers.infrastructure.persistence.repository;

import com.kata.customers.auth.RefreshToken;
import com.kata.customers.user.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(AppUser user);
}
