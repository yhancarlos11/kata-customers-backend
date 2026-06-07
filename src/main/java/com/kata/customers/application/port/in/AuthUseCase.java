package com.kata.customers.application.port.in;

import com.kata.customers.auth.AuthMeResponse;
import com.kata.customers.auth.AuthResponse;
import com.kata.customers.auth.LoginRequest;
import com.kata.customers.auth.LogoutResponse;
import com.kata.customers.auth.RegisterRequest;

public interface AuthUseCase {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthMeResponse me(String username);

    AuthResponse refresh(String refreshTokenValue);

    LogoutResponse logout(String token, String refreshTokenValue);
}
