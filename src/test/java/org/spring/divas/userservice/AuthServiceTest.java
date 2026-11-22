package org.spring.divas.userservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.spring.divas.userservice.common.enums.UserRole;
import org.spring.divas.userservice.feature.auth.AuthRequest;
import org.spring.divas.userservice.feature.auth.AuthService;
import org.spring.divas.userservice.feature.auth.JwtService;
import org.spring.divas.userservice.feature.manager.VenueManager;
import org.spring.divas.userservice.feature.manager.VenueManagerRepository;
import org.spring.divas.userservice.feature.user.User;
import org.spring.divas.userservice.feature.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VenueManagerRepository managerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded_pass")
                .role(UserRole.CUSTOMER)
                .build();
    }

    @Test
    void authenticate_shouldReturnTokenWhenCredentialsValid() {
        AuthRequest request = new AuthRequest("test@example.com", "plain_pass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain_pass", "encoded_pass")).thenReturn(true);
        when(jwtService.generateAccessToken(eq("1"), anyList())).thenReturn("mock_token");

        String token = authService.authenticate(request);

        assertThat(token).isEqualTo("mock_token");
        verify(jwtService).generateAccessToken("1", List.of(UserRole.CUSTOMER.name()));
    }

    @Test
    void authenticate_shouldThrowWhenUserNotFound() {
        AuthRequest request = new AuthRequest("unknown@example.com", "pass");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void authenticate_shouldThrowWhenPasswordDoesNotMatch() {
        AuthRequest request = new AuthRequest("test@example.com", "wrong_pass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_pass", "encoded_pass")).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void authenticate_shouldIncludeVenueRolesForManager() {
        user.setRole(UserRole.MANAGER);
        AuthRequest request = new AuthRequest("test@example.com", "plain_pass");
        VenueManager vm = new VenueManager(1L, 10L, ManagerRole.SENIOR);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain_pass", "encoded_pass")).thenReturn(true);
        when(managerRepository.findByUserId(1L)).thenReturn(List.of(vm));
        when(jwtService.generateAccessToken(eq("1"), anyList())).thenReturn("manager_token");

        String token = authService.authenticate(request);

        assertThat(token).isEqualTo("manager_token");
        verify(jwtService).generateAccessToken("1", List.of(UserRole.MANAGER.name(), "SENIOR 10"));
    }

    @Test
    void authenticate_shouldHandleAdminRole() {
        user.setRole(UserRole.ADMIN);
        AuthRequest request = new AuthRequest("test@example.com", "plain_pass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain_pass", "encoded_pass")).thenReturn(true);
        when(jwtService.generateAccessToken(eq("1"), anyList())).thenReturn("admin_token");

        String token = authService.authenticate(request);

        assertThat(token).isEqualTo("admin_token");
        verify(jwtService).generateAccessToken("1", List.of(UserRole.ADMIN.name()));
    }
}
