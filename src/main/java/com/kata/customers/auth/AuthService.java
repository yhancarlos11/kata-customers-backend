package com.kata.customers.auth;

import com.kata.customers.application.port.in.AuthUseCase;
import com.kata.customers.application.port.out.AuthenticationPort;
import com.kata.customers.application.port.out.AuthUserPort;
import com.kata.customers.application.port.out.PasswordHashPort;
import com.kata.customers.application.port.out.RefreshTokenPort;
import com.kata.customers.application.port.out.TokenPort;
import com.kata.customers.application.port.out.TokenRevocationPort;
import com.kata.customers.common.ResourceNotFoundException;
import com.kata.customers.user.AppUser;
import com.kata.customers.user.UserRole;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements AuthUseCase {

    private final AuthUserPort authUserPort;
    private final PasswordHashPort passwordHashPort;
    private final TokenPort tokenPort;
    private final AuthenticationPort authenticationPort;
    private final TokenRevocationPort tokenRevocationPort;
    private final RefreshTokenPort refreshTokenPort;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    public AuthService(
        AuthUserPort authUserPort,
        PasswordHashPort passwordHashPort,
        TokenPort tokenPort,
        AuthenticationPort authenticationPort,
        TokenRevocationPort tokenRevocationPort,
        RefreshTokenPort refreshTokenPort
    ) {
        this.authUserPort = authUserPort;
        this.passwordHashPort = passwordHashPort;
        this.tokenPort = tokenPort;
        this.authenticationPort = authenticationPort;
        this.tokenRevocationPort = tokenRevocationPort;
        this.refreshTokenPort = refreshTokenPort;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (authUserPort.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }

        if (authUserPort.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordHashPort.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        AppUser saved = authUserPort.save(user);
        return issueTokens(saved);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationPort.authenticate(request.getUsername(), request.getPassword());

        AppUser user = authUserPort
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        return issueTokens(user);
    }

    @Override
    public AuthMeResponse me(String username) {
        AppUser user = authUserPort
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return new AuthMeResponse(user.getUsername(), user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenPort
            .findByToken(refreshTokenValue)
            .orElseThrow(() -> new BadCredentialsException("Refresh token invalido"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadCredentialsException("Refresh token invalido o expirado");
        }

        AppUser user = refreshToken.getUser();
        refreshToken.setRevoked(true);
        refreshTokenPort.save(refreshToken);

        return issueTokens(user);
    }

    @Override
    @Transactional
    public LogoutResponse logout(String token, String refreshTokenValue) {
        Instant expiresAt = tokenPort.extractExpiration(token);
        tokenRevocationPort.revoke(token, expiresAt);

        if (refreshTokenValue != null && !refreshTokenValue.isBlank()) {
            refreshTokenPort.findByToken(refreshTokenValue).ifPresent(stored -> {
                stored.setRevoked(true);
                refreshTokenPort.save(stored);
            });
        }

        return new LogoutResponse("Sesion cerrada correctamente");
    }

    private AuthResponse issueTokens(AppUser user) {
        String accessToken = tokenPort.generateToken(user);
        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshToken.setRevoked(false);
        refreshTokenPort.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenValue);
    }
}
