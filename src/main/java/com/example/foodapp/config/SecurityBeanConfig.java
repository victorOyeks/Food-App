package com.example.foodapp.config;

import com.example.foodapp.entities.Admin;
import com.example.foodapp.entities.Company;
import com.example.foodapp.entities.User;
import com.example.foodapp.entities.Vendor;
import com.example.foodapp.repository.AdminRepository;
import com.example.foodapp.repository.CompanyRepository;
import com.example.foodapp.repository.UserRepository;
import com.example.foodapp.repository.VendorRepository;
import com.example.foodapp.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityBeanConfig {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmail(username);
            Vendor vendor = vendorRepository.findByEmail(username);
            Company company = companyRepository.findByCompanyEmail(username);
            Admin admin = adminRepository.findByEmail(username);
            return new UserDetailsImpl(user, vendor, admin, company);
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passWordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passWordEncoder() {
        return new BCryptPasswordEncoder();
    }
}