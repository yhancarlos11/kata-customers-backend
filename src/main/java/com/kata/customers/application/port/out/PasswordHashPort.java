package com.kata.customers.application.port.out;

public interface PasswordHashPort {

    String encode(String rawPassword);
}
