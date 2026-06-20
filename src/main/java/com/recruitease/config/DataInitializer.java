package com.recruitease.config;

import com.recruitease.model.User;
import com.recruitease.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default Admin if not exists
        if (!userRepository.existsByEmail("admin@recruitease.com")) {
            User admin = new User();
            admin.setEmail("admin@recruitease.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Admin");
            admin.setRole(User.Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("✅ Default Admin created: admin@recruitease.com / admin123");
        }
    }
}
