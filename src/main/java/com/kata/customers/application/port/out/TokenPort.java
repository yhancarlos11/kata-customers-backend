package com.kata.customers.application.port.out;

import com.kata.customers.user.AppUser;
import java.time.Instant;

public interface TokenPort {

    String generateToken(AppUser user);

    Instant extractExpiration(String token);
}
