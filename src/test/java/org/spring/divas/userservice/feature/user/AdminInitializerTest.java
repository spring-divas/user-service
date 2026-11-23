package org.spring.divas.userservice.feature.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateAdminWithEncodedPasswordWhenMissing() {
        when(userRepository.findByEmail("admin@feedo.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("admin-password")).thenReturn("encoded-password");

        initializer("admin@feedo.com", "admin-password").run(null);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getEmail()).isEqualTo("admin@feedo.com");
        assertThat(saved.getValue().getPassword()).isEqualTo("encoded-password");
        assertThat(saved.getValue().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void shouldNotCreateAdminWhenEmailIsAlreadyUsed() {
        when(userRepository.findByEmail("admin@feedo.com")).thenReturn(Optional.of(new User()));

        initializer("admin@feedo.com", "admin-password").run(null);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldSkipWhenAdminIsNotConfigured() {
        initializer("", "admin-password").run(null);
        initializer("admin@feedo.com", "").run(null);

        verifyNoInteractions(userRepository, passwordEncoder);
    }

    private AdminInitializer initializer(String email, String password) {
        return new AdminInitializer(userRepository, passwordEncoder, email, password);
    }
}
