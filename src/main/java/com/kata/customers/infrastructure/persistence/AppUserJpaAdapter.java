package com.kata.customers.infrastructure.persistence;

import com.kata.customers.application.port.out.AuthUserPort;
import com.kata.customers.infrastructure.persistence.repository.AppUserRepository;
import com.kata.customers.user.AppUser;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AppUserJpaAdapter implements AuthUserPort {

    private final AppUserRepository appUserRepository;

    public AppUserJpaAdapter(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return appUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return appUserRepository.existsByEmail(email);
    }

    @Override
    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }
}
