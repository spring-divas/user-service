package org.spring.divas.userservice.feature.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.spring.divas.userservice.common.enums.UserRole;
import org.spring.divas.userservice.feature.manager.VenueManager;
import org.spring.divas.userservice.feature.manager.VenueManagerRepository;
import org.spring.divas.userservice.feature.user.User;
import org.spring.divas.userservice.feature.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private VenueManagerRepository managerRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @InjectMocks private AuthService service;

    @Test
    void authenticateIssuesCustomerTokenWithCustomerRole() {
        User user = user(14L, UserRole.CUSTOMER);
        AuthRequest request = request("customer@example.com", "secret");
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded-secret")).thenReturn(true);
        when(jwtService.generateAccessToken("14", List.of("CUSTOMER"))).thenReturn("signed-token");

        assertThat(service.authenticate(request)).isEqualTo("signed-token");

        verify(managerRepository, never()).findByUserId(14L);
        verify(jwtService).generateAccessToken("14", List.of("CUSTOMER"));
    }

    @Test
    void authenticateIssuesAdminTokenWithAdminRole() {
        User user = user(15L, UserRole.ADMIN);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded-secret")).thenReturn(true);
        when(jwtService.generateAccessToken("15", List.of("ADMIN"))).thenReturn("admin-token");

        assertThat(service.authenticate(request("admin@example.com", "secret")))
                .isEqualTo("admin-token");

        verify(managerRepository, never()).findByUserId(15L);
    }

    @Test
    void authenticateAddsVenueSpecificRolesForManager() {
        User user = user(16L, UserRole.MANAGER);
        when(userRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded-secret")).thenReturn(true);
        when(managerRepository.findByUserId(16L)).thenReturn(List.of(
                VenueManager.builder().userId(16L).venueId(8L)
                        .managerLevel(ManagerRole.SENIOR).build(),
                VenueManager.builder().userId(16L).venueId(9L)
                        .managerLevel(ManagerRole.JUNIOR).build()));
        when(jwtService.generateAccessToken("16", List.of("MANAGER", "SENIOR 8", "JUNIOR 9")))
                .thenReturn("manager-token");

        assertThat(service.authenticate(request("manager@example.com", "secret")))
                .isEqualTo("manager-token");

        verify(jwtService).generateAccessToken("16", List.of("MANAGER", "SENIOR 8", "JUNIOR 9"));
    }

    @Test
    void authenticateRejectsIncorrectPasswordWithoutIssuingToken() {
        User user = user(17L, UserRole.CUSTOMER);
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-secret")).thenReturn(false);

        AuthRequest wrongPasswordRequest = request("customer@example.com", "wrong");
        assertThatThrownBy(() -> service.authenticate(wrongPasswordRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(jwtService, never()).generateAccessToken(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyList());
    }

    @Test
    void authenticateRejectsUnknownEmail() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        AuthRequest missingUserRequest = request("missing@example.com", "secret");
        assertThatThrownBy(() -> service.authenticate(missingUserRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");

        verify(passwordEncoder, never()).matches(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }

    private static User user(Long id, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        user.setPassword("encoded-secret");
        return user;
    }

    private static AuthRequest request(String email, String password) {
        return new AuthRequest(email, password);
    }
}
