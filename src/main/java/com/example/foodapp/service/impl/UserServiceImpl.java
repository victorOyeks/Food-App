package com.example.foodapp.service.impl;

import com.example.foodapp.dto.request.*;
import com.example.foodapp.dto.response.LoginResponse;
import com.example.foodapp.dto.response.UserResponse;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.AdminRepository;
import com.example.foodapp.repository.CompanyRepository;
import com.example.foodapp.repository.UserRepository;
import com.example.foodapp.repository.VendorRepository;
import com.example.foodapp.security.JwtService;
import com.example.foodapp.service.EmailService;
import com.example.foodapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final VendorRepository vendorRepository;
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;

    @Override
    public UserResponse signup(RegistrationRequest request) {

        boolean isExistingStaff = userRepository.existsByEmail(request.getEmail());
        if (!isExistingStaff) {
            throw new CustomException("Contact Admin for signup!!!");
        }

        boolean isExistingVendor = vendorRepository.existsByEmail(request.getEmail());
        if (isExistingVendor) {
            throw new CustomException("Vendor with " + request.getEmail() + " already exists");
        }

        boolean isExistingAdmin = adminRepository.existsByEmail(request.getEmail());
        if (isExistingAdmin) {
            throw new CustomException("User already exist with the email: " + request.getEmail());
        }

        boolean isExistingCompany = companyRepository.existsByCompanyEmail(request.getEmail());
        if (isExistingCompany) {
            throw new CustomException("Company with this email already exist");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("Password does not match");
        }

        String verificationToken = generateVerificationToken();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User existingStaff = userRepository.findByEmail(request.getEmail());


        if(existingStaff.getPassword() != null) {
            throw new CustomException("User already exist!!!");
        }

        existingStaff.setFirstName(request.getFirstName());
        existingStaff.setLastName(request.getLastName());
        existingStaff.setPhone(request.getPhone());
        existingStaff.setPassword(encodedPassword);
        existingStaff.setEnabled(true);

        userRepository.save(existingStaff);

        return UserResponse.builder()
                .userId(existingStaff.getId())
                .firstName(existingStaff.getFirstName())
                .lastName(existingStaff.getLastName())
                .email(existingStaff.getEmail())
                .build();
        }

    @Override
    public String verifyAccount(String verificationToken) {
        User user = userRepository.findByVerificationToken(verificationToken);
        if (user != null) {
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return "Account verified successfully. Proceed to login.";
        }
        throw new CustomException("Invalid verification token or email.");
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new CustomException("User with " + email + " does not exist");
        }

        if (!user.getEnabled()) {
            throw new CustomException("User with " + email + " is not enabled");
        }

        if(user.getLocked()) {
            throw new CustomException("Account has been locked! Contact support");
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String accessToken = jwtService.generateToken(createAuthentication(user.getEmail(), user.getPassword()));
            String refreshToken = jwtService.generateRefreshToken(createAuthentication(user.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(createAuthentication(user.getEmail(), user.getPassword()));

            return LoginResponse.builder()
                    .email(user.getEmail())
                    .message(user.getRole() + " logged in successfully!!!")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new CustomException("Invalid username or password");
    }

    @Override
    public String forgotPassword(String email) throws IOException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new CustomException("User with " + email + " does not exist");
        }

        // Generate and save the password reset token
        String resetToken = generateResetToken();
        user.setVerificationToken(resetToken);
        userRepository.save(user);

        // Send password reset email
        sendPasswordResetEmail(email, resetToken);

        return "Password reset link has been sent to your email address!!!.";
    }

    @Override
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String resetToken = resetPasswordRequest.getResetToken();
        String newPassword = resetPasswordRequest.getNewPassword();

        User user = userRepository.findByVerificationToken(resetToken);
        Vendor vendor = vendorRepository.findByVerificationToken(resetToken);
        Company company = companyRepository.findByVerificationToken(resetToken);
        Admin admin = adminRepository.findByEmail(resetToken);

        if (user != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            user.setVerificationToken(null);
            userRepository.save(user);
            return "User password reset successful.";
        } else if (vendor != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            vendor.setPassword(encodedPassword);
            vendor.setVerificationToken(null);
            vendorRepository.save(vendor);
            return "Vendor password reset successful.";
        } else if (company != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            company.setPassword(encodedPassword);
            companyRepository.save(company);
            return "Company password reset successfully.";
        } else if (admin != null) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            admin.setPassword(encodedPassword);
            adminRepository.save(admin);
            return "Admin password reset successfully";
        }
        else {
            throw new CustomException("Invalid password reset token.");
        }
    }

    private void sendVerificationEmail(String recipient, String verificationToken) throws IOException {
        String verificationLink = "http://localhost:9191/api/auth/verify?token=" + verificationToken;
        String subject = "Account Verification";
        String messageBody = "Please click on the link below to verify your account:\n" + verificationLink;
        EmailDetails emailDetails = new EmailDetails(recipient, subject, messageBody);
        emailService.sendEmail(emailDetails);
    }

    private void sendPasswordResetEmail(String recipient, String resetCode) throws IOException {
        String subject = "Password Reset";
        String messageBody = "Your password reset code is :" + resetCode;
        EmailDetails emailDetails = new EmailDetails(recipient, subject, messageBody);
        emailService.sendEmail(emailDetails);
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    private String generateResetToken() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        return String.format("%06d", randomNumber);
    }

    private Authentication createAuthentication(String username, String password) {
        return new UsernamePasswordAuthenticationToken(username, password);
    }
}