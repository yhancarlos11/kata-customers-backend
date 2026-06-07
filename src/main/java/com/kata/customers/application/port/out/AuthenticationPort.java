package com.kata.customers.application.port.out;

public interface AuthenticationPort {

    void authenticate(String username, String password);
}
