package com.kata.customers.application.port.out;

import com.kata.customers.user.AppUser;
import java.util.Optional;

public interface AuthUserPort {

    Optional<AppUser> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    AppUser save(AppUser user);
}
