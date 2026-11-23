package org.spring.divas.userservice.feature.user;

import lombok.extern.slf4j.Slf4j;
import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Creates the initial ADMIN account from {@code ADMIN_EMAIL} and {@code ADMIN_PASSWORD},
 * so that the first administrator can log in and create other accounts.
 */
@Slf4j
@Component
public class AdminInitializer implements ApplicationRunner {

    private static final String ADMIN_NAME = "Admin";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.email:}") String email,
            @Value("${admin.password:}") String password
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            log.info("Initial admin account is not configured, skipping");
            return;
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        User admin = new User();
        admin.setFirstName(ADMIN_NAME);
        admin.setLastName(ADMIN_NAME);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
        log.info("Created the initial admin account");
    }
}
