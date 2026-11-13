
package com.ekabotdev.library.config;

import com.ekabotdev.library.enums.Role;
import com.ekabotdev.library.user.entity.User;
import com.ekabotdev.library.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String initialPassword;

    public AdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.email}") String adminEmail,
            @Value("${admin.initial-password}") String initialPassword
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.initialPassword = initialPassword;
    }

    @Override
    public void run(String... args) {

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Development administrator already exists. Skipping creation.");
            return;
        }

        User admin = new User();

        admin.setName("Library Admin");
        admin.setEmail(adminEmail);
        admin.setPasswordHash(
                passwordEncoder.encode(initialPassword)
        );
        admin.setRole(Role.ADMIN);

        userRepository.save(admin);

        log.info("Development administrator created successfully.");
    }
}

