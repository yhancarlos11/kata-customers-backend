package com.kata.customers.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kata.customers.infrastructure.persistence.repository.AppUserRepository;
import com.kata.customers.user.AppUser;
import com.kata.customers.user.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppUserJpaAdapterTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    void findByUsernameShouldDelegateToRepository() {
        AppUserJpaAdapter adapter = new AppUserJpaAdapter(appUserRepository);
        AppUser user = new AppUser(1L, "demo", "demo@correo.com", "hashed", UserRole.USER);
        when(appUserRepository.findByUsername("demo")).thenReturn(Optional.of(user));

        Optional<AppUser> result = adapter.findByUsername("demo");

        assertTrue(result.isPresent());
        assertEquals("demo", result.get().getUsername());
        verify(appUserRepository).findByUsername("demo");
    }

    @Test
    void existsAndSaveShouldDelegateToRepository() {
        AppUserJpaAdapter adapter = new AppUserJpaAdapter(appUserRepository);
        AppUser user = new AppUser(1L, "demo", "demo@correo.com", "hashed", UserRole.USER);

        when(appUserRepository.existsByUsername("demo")).thenReturn(true);
        when(appUserRepository.existsByEmail("demo@correo.com")).thenReturn(true);
        when(appUserRepository.save(user)).thenReturn(user);

        assertTrue(adapter.existsByUsername("demo"));
        assertTrue(adapter.existsByEmail("demo@correo.com"));
        AppUser saved = adapter.save(user);

        assertSame(user, saved);
        verify(appUserRepository).existsByUsername("demo");
        verify(appUserRepository).existsByEmail("demo@correo.com");
        verify(appUserRepository).save(user);
    }
}
