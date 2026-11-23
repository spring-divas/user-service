package org.spring.divas.userservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.common.enums.ManagerRole;
import org.spring.divas.userservice.common.enums.UserRole;
import org.spring.divas.userservice.feature.allergy.UserAllergyRepository;
import org.spring.divas.userservice.feature.allergy.UserAllergyService;
import org.spring.divas.userservice.feature.auth.AuthRequest;
import org.spring.divas.userservice.feature.auth.AuthService;
import org.spring.divas.userservice.feature.manager.VenueManager;
import org.spring.divas.userservice.feature.manager.VenueManagerRepository;
import org.spring.divas.userservice.feature.manager.VenueManagerService;
import org.spring.divas.userservice.feature.manager.VenueManagerUpdateDto;
import org.spring.divas.userservice.feature.user.ChangePasswordDto;
import org.spring.divas.userservice.feature.user.UserCreateDto;
import org.spring.divas.userservice.feature.user.UserRepository;
import org.spring.divas.userservice.feature.user.UserResponseDto;
import org.spring.divas.userservice.feature.user.UserService;
import org.spring.divas.userservice.feature.user.UserUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.security.interfaces.RSAPublicKey;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestJwtConfig.class)
class ServiceIntegrationTest {

    @Autowired private UserService userService;
    @Autowired private AuthService authService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserAllergyService allergyService;
    @Autowired private UserAllergyRepository allergyRepository;
    @Autowired private VenueManagerService venueManagerService;
    @Autowired private VenueManagerRepository venueManagerRepository;
    @Autowired private RSAPublicKey publicKey;

    @BeforeEach
    void cleanDatabase() {
        venueManagerRepository.deleteAll();
        allergyRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void userServicePersistsUpdatesAndDeletesUserWithEncodedPasswords() {
        UserResponseDto created = userService.create(
                user("Alice", "Jones", "alice@example.com", "password-123"), UserRole.CUSTOMER);

        assertThat(created.id()).isNotNull();
        assertThat(created.role()).isEqualTo(UserRole.CUSTOMER);
        assertThat(userRepository.findById(created.id())).get()
                .satisfies(persisted -> {
                    assertThat(persisted.getFirstName()).isEqualTo("Alice");
                    assertThat(persisted.getEmail()).isEqualTo("alice@example.com");
                    assertThat(passwordEncoder.matches("password-123", persisted.getPassword()))
                            .isTrue();
                });

        UserUpdateDto update = new UserUpdateDto("Alicia", "Jones", "alicia@example.com");
        UserResponseDto updated = userService.update(created.id(), update);
        assertThat(updated.firstName()).isEqualTo("Alicia");
        assertThat(updated.email()).isEqualTo("alicia@example.com");

        ChangePasswordDto password = new ChangePasswordDto("replacement-456");
        userService.updatePassword(created.id(), password);
        assertThat(passwordEncoder.matches("replacement-456",
                userRepository.findById(created.id()).orElseThrow().getPassword())).isTrue();

        userService.delete(created.id());
        assertThat(userRepository.findById(created.id())).isEmpty();
    }

    @Test
    void allergyServicePersistsQueriesAndDeletesAllergies() {
        allergyService.create(41L, 101L);
        allergyService.create(41L, 102L);
        allergyService.create(42L, 103L);

        assertThat(allergyService.findAllByUserId(41L)).containsExactlyInAnyOrder(101L, 102L);
        assertThat(allergyService.hasAnyAllergy(41L, List.of(103L, 102L))).isTrue();
        assertThat(allergyService.hasAnyAllergy(41L, List.of(103L))).isFalse();

        allergyService.delete(41L, 101L);
        assertThat(allergyService.findAllByUserId(41L)).containsExactly(102L);
        allergyService.deleteAllByUserId(41L);
        assertThat(allergyRepository.findByUserId(41L)).isEmpty();
        assertThat(allergyRepository.findByUserId(42L)).hasSize(1);
    }

    @Test
    void venueManagerServiceFindsUpdatesAndDeletesPersistedAssignments() {
        venueManagerRepository.save(VenueManager.builder().userId(51L).venueId(7L)
                .managerLevel(ManagerRole.JUNIOR).build());
        assertThat(venueManagerService.findAllByUserAndVenueId(51L, 7L))
                .singleElement()
                .satisfies(result -> assertThat(result.managerLevel())
                        .isEqualTo(ManagerRole.JUNIOR));

        VenueManagerUpdateDto update = new VenueManagerUpdateDto(51L, 7L, ManagerRole.SENIOR);
        venueManagerService.update(update);
        assertThat(venueManagerService.findAllByUserAndVenueId(51L, 7L))
                .singleElement()
                .satisfies(result -> assertThat(result.managerLevel())
                        .isEqualTo(ManagerRole.SENIOR));

        venueManagerRepository.save(VenueManager.builder().userId(51L).venueId(8L)
                .managerLevel(ManagerRole.JUNIOR).build());
        venueManagerService.delete(51L, 7L);
        assertThat(venueManagerRepository.findByUserId(51L))
                .extracting(manager -> manager.getVenueId())
                .containsExactly(8L);
    }

    @Test
    void authenticationIssuesVerifiableJwtWithPersistedManagerRoles() {
        UserResponseDto created = userService.create(
                user("Morgan", "Manager", "morgan@example.com", "password-789"), UserRole.MANAGER);
        venueManagerRepository.save(VenueManager.builder().userId(created.id()).venueId(17L)
                .managerLevel(ManagerRole.SENIOR).build());
        venueManagerRepository.save(VenueManager.builder().userId(created.id()).venueId(18L)
                .managerLevel(ManagerRole.JUNIOR).build());

        AuthRequest request = new AuthRequest("morgan@example.com", "password-789");

        String token = authService.authenticate(request);
        JwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        Jwt jwt = decoder.decode(token);

        assertThat(jwt.getSubject()).isEqualTo(created.id().toString());
        assertThat(jwt.getClaims().get("iss")).isEqualTo("user-service");
        assertThat(jwt.getClaimAsStringList("roles"))
                .containsExactlyInAnyOrder("MANAGER", "SENIOR 17", "JUNIOR 18");
        assertThat(jwt.getExpiresAt()).isAfter(jwt.getIssuedAt());
    }

    private static UserCreateDto user(
            String firstName, String lastName, String email, String password
    ) {
        return new UserCreateDto(firstName, lastName, email, password);
    }

}
