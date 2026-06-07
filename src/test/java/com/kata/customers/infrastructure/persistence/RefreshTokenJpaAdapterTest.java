package com.kata.customers.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kata.customers.auth.RefreshToken;
import com.kata.customers.infrastructure.persistence.repository.RefreshTokenRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenJpaAdapterTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldDelegateRefreshTokenOperations() {
        RefreshTokenJpaAdapter adapter = new RefreshTokenJpaAdapter(refreshTokenRepository);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-1");

        when(refreshTokenRepository.findByToken("refresh-1")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        assertTrue(adapter.findByToken("refresh-1").isPresent());
        assertSame(refreshToken, adapter.save(refreshToken));

        verify(refreshTokenRepository).findByToken("refresh-1");
        verify(refreshTokenRepository).save(refreshToken);
    }
}
