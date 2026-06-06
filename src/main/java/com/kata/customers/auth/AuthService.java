package com.kata.customers.auth;

import com.kata.customers.common.ResourceNotFoundException;
import com.kata.customers.security.JwtService;
import com.kata.customers.security.TokenBlacklistService;
import com.kata.customers.user.AppUser;
import com.kata.customers.user.AppUserRepository;
import com.kata.customers.user.UserRole;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthService(
        AppUserRepository appUserRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationManager authenticationManager,
        TokenBlacklistService tokenBlacklistService
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }

        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        AppUser saved = appUserRepository.save(user);
        String token = jwtService.generateToken(toUserDetails(saved));
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        AppUser user = appUserRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        String token = jwtService.generateToken(toUserDetails(user));
        return new AuthResponse(token);
    }

    public AuthMeResponse me(String username) {
        AppUser user = appUserRepository
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return new AuthMeResponse(user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public LogoutResponse logout(String token) {
        Instant expiresAt = jwtService.extractExpiration(token).toInstant();
        tokenBlacklistService.revoke(token, expiresAt);
        return new LogoutResponse("Sesion cerrada correctamente");
    }

    private UserDetails toUserDetails(AppUser user) {
        return User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }
}
