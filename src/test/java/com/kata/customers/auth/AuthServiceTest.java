package com.kata.customers.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserPort authUserPort;

    @Mock
    private PasswordHashPort passwordHashPort;

    @Mock
    private TokenPort tokenPort;

    @Mock
    private AuthenticationPort authenticationPort;

    @Mock
    private TokenRevocationPort tokenRevocationPort;

    @Mock
    private RefreshTokenPort refreshTokenPort;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
            authUserPort,
            passwordHashPort,
            tokenPort,
            authenticationPort,
            tokenRevocationPort,
            refreshTokenPort
        );
        ReflectionTestUtils.setField(authService, "refreshTokenExpirationMs", 600000L);
    }

    @Test
    void registerShouldCreateUserAndIssueTokens() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("demoUser");
        request.setEmail("demo@correo.com");
        request.setPassword("Secret123*");

        AppUser savedUser = new AppUser(1L, "demoUser", "demo@correo.com", "hashed", UserRole.USER);

        when(authUserPort.existsByUsername("demoUser")).thenReturn(false);
        when(authUserPort.existsByEmail("demo@correo.com")).thenReturn(false);
        when(passwordHashPort.encode("Secret123*")).thenReturn("hashed");
        when(authUserPort.save(any(AppUser.class))).thenReturn(savedUser);
        when(tokenPort.generateToken(savedUser)).thenReturn("access-token");

        AuthResponse response = authService.register(request);

        assertEquals("access-token", response.getToken());
        assertNotNull(response.getRefreshToken());
        verify(authUserPort).save(any(AppUser.class));

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenPort).save(refreshTokenCaptor.capture());
        RefreshToken stored = refreshTokenCaptor.getValue();
        assertFalse(stored.isRevoked());
        assertEquals(savedUser, stored.getUser());
        assertNotNull(stored.getExpiresAt());
    }

    @Test
    void registerShouldFailWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("taken");
        request.setEmail("demo@correo.com");
        request.setPassword("Secret123*");

        when(authUserPort.existsByUsername("taken")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        verify(authUserPort, never()).save(any(AppUser.class));
        verify(refreshTokenPort, never()).save(any(RefreshToken.class));
    }

    @Test
    void registerShouldFailWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("demoUser");
        request.setEmail("used@correo.com");
        request.setPassword("Secret123*");

        when(authUserPort.existsByUsername("demoUser")).thenReturn(false);
        when(authUserPort.existsByEmail("used@correo.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        verify(authUserPort, never()).save(any(AppUser.class));
    }

    @Test
    void loginShouldAuthenticateAndIssueTokens() {
        LoginRequest request = new LoginRequest();
        request.setUsername("demoUser");
        request.setPassword("Secret123*");

        AppUser user = new AppUser(1L, "demoUser", "demo@correo.com", "hashed", UserRole.USER);
        when(authUserPort.findByUsername("demoUser")).thenReturn(Optional.of(user));
        when(tokenPort.generateToken(user)).thenReturn("access-token");

        AuthResponse response = authService.login(request);

        assertEquals("access-token", response.getToken());
        assertNotNull(response.getRefreshToken());
        verify(authenticationPort).authenticate("demoUser", "Secret123*");
        verify(refreshTokenPort).save(any(RefreshToken.class));
    }

    @Test
    void loginShouldFailWhenUserIsNotFoundAfterAuthentication() {
        LoginRequest request = new LoginRequest();
        request.setUsername("ghost");
        request.setPassword("Secret123*");

        when(authUserPort.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));

        verify(authenticationPort).authenticate("ghost", "Secret123*");
        verify(refreshTokenPort, never()).save(any(RefreshToken.class));
    }

    @Test
    void meShouldReturnAuthenticatedUserData() {
        AppUser user = new AppUser(1L, "demoUser", "demo@correo.com", "hashed", UserRole.USER);
        when(authUserPort.findByUsername("demoUser")).thenReturn(Optional.of(user));

        AuthMeResponse response = authService.me("demoUser");

        assertEquals("demoUser", response.username());
        assertEquals("demo@correo.com", response.email());
        assertEquals("USER", response.role());
    }

    @Test
    void meShouldFailWhenUserNotFound() {
        when(authUserPort.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.me("ghost"));
    }

    @Test
    void refreshShouldFailWhenRefreshTokenDoesNotExist() {
        when(refreshTokenPort.findByToken("missing")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.refresh("missing"));
    }

    @Test
    void refreshShouldFailWhenRefreshTokenIsRevoked() {
        RefreshToken stored = new RefreshToken();
        stored.setToken("revoked");
        stored.setRevoked(true);
        stored.setExpiresAt(Instant.now().plusSeconds(300));

        when(refreshTokenPort.findByToken("revoked")).thenReturn(Optional.of(stored));

        assertThrows(BadCredentialsException.class, () -> authService.refresh("revoked"));
    }

    @Test
    void refreshShouldRevokeCurrentTokenAndIssueNewPair() {
        AppUser user = new AppUser(1L, "demoUser", "demo@correo.com", "hashed", UserRole.USER);
        RefreshToken current = new RefreshToken();
        current.setToken("refresh-old");
        current.setUser(user);
        current.setRevoked(false);
        current.setExpiresAt(Instant.now().plusSeconds(300));

        when(refreshTokenPort.findByToken("refresh-old")).thenReturn(Optional.of(current));
        when(tokenPort.generateToken(user)).thenReturn("access-new");

        AuthResponse response = authService.refresh("refresh-old");

        assertEquals("access-new", response.getToken());
        assertNotNull(response.getRefreshToken());
        assertTrue(current.isRevoked());
        verify(refreshTokenPort).save(current);
    }

    @Test
    void logoutShouldRevokeAccessAndRefreshTokens() {
        Instant expiresAt = Instant.parse("2026-06-07T22:00:00Z");
        RefreshToken stored = new RefreshToken();
        stored.setToken("refresh-token");
        stored.setRevoked(false);

        when(tokenPort.extractExpiration("access-token")).thenReturn(expiresAt);
        when(refreshTokenPort.findByToken("refresh-token")).thenReturn(Optional.of(stored));

        LogoutResponse response = authService.logout("access-token", "refresh-token");

        assertEquals("Sesion cerrada correctamente", response.message());
        assertTrue(stored.isRevoked());
        verify(tokenRevocationPort).revoke("access-token", expiresAt);
        verify(refreshTokenPort).save(stored);
    }

    @Test
    void logoutShouldRevokeOnlyAccessTokenWhenRefreshIsBlank() {
        Instant expiresAt = Instant.parse("2026-06-07T22:00:00Z");
        when(tokenPort.extractExpiration("access-token")).thenReturn(expiresAt);

        LogoutResponse response = authService.logout("access-token", "  ");

        assertEquals("Sesion cerrada correctamente", response.message());
        verify(tokenRevocationPort).revoke("access-token", expiresAt);
        verify(refreshTokenPort, never()).findByToken(any(String.class));
    }
}
