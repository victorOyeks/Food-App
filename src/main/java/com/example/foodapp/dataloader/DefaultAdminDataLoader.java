package com.example.foodapp.dataloader;

import com.example.foodapp.constant.ROLE;
import com.example.foodapp.entities.Admin;
import com.example.foodapp.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAdminDataLoader implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;



    @Override
    public void run(String... args) {
        if (adminRepository.existsByEmail("tm30admin@foodapp.com")) {
            return; // Admin already exists, no need to create
        }
        String encodedPassword = passwordEncoder.encode("password1");
        // Create a new default admin
        Admin admin = Admin.builder()
                .email("tm30admin@foodapp.com")
                .password(encodedPassword)
                .enabled(true)
                .locked(false)
                .role(ROLE.SUPER_ADMIN)
                .build();
        // Save the admin to the database
        adminRepository.save(admin);
    }
}
