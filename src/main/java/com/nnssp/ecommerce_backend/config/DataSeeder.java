package com.nnssp.ecommerce_backend.config;

import com.nnssp.ecommerce_backend.entity.User;
import com.nnssp.ecommerce_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@ecommerce.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@ecommerce.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Default ADMIN user created: admin@ecommerce.com / admin123");
        }
    }
}
