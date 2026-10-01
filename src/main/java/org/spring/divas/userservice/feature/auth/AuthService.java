package org.spring.divas.userservice.feature.auth;

import lombok.RequiredArgsConstructor;
import org.spring.divas.userservice.common.enums.UserRole;
import org.spring.divas.userservice.feature.manager.VenueManagerRepository;
import org.spring.divas.userservice.feature.user.User;
import org.spring.divas.userservice.feature.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final VenueManagerRepository managerRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public String authenticate(AuthRequest request) {
        User user = userRepository
                .findByEmail(request.email)
                .orElseThrow();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return jwtService.generateAccessToken(
                String.valueOf(user.getId()),
                getUserRoles(user)
        );
    }

    private List<String> getUserRoles(User user) {
        return switch (user.getRole()) {
            case ADMIN -> List.of(UserRole.ADMIN.name());
            case CUSTOMER -> List.of(UserRole.CUSTOMER.name());
            case MANAGER -> Stream.concat(
                    Stream.of(UserRole.MANAGER.name()),
                    managerRepository.findByUserId(user.getId()).stream()
                            .map(manager -> manager.getManagerLevel()
                                    + " " + manager.getVenueId())
            ).toList();
        };
    }
}