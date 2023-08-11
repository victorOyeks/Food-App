package com.example.foodapp.service.impl;

import com.example.foodapp.utils.CustomFileHandler;
import com.example.foodapp.constant.ROLE;
import com.example.foodapp.dto.request.EmailDetails;
import com.example.foodapp.dto.request.LoginRequest;
import com.example.foodapp.dto.request.ResetEmail;
import com.example.foodapp.dto.request.ResetPasswordRequest;
import com.example.foodapp.dto.response.LoginResponse;
import com.example.foodapp.entities.Admin;
import com.example.foodapp.entities.Company;
import com.example.foodapp.entities.User;
import com.example.foodapp.entities.Vendor;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.AdminRepository;
import com.example.foodapp.repository.CompanyRepository;
import com.example.foodapp.repository.UserRepository;
import com.example.foodapp.repository.VendorRepository;
import com.example.foodapp.security.JwtService;
import com.example.foodapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final CompanyRepository companyRepository;
    private final EmailServiceImpl emailService;

    //private static final Logger logger = Logger.getLogger(AuthServiceImpl.class.getName());

    @Override
    public String vendorAdminSignup(String email, String token) {
        Vendor vendor = vendorRepository.findByEmailAndSignupToken(email, token);
        if (vendor == null) {
            throw new CustomException("Invalid Vendor. Contact Admin!");
        }
        vendor.setEnabled(true);
        vendor.setSignupToken(null);
        vendorRepository.save(vendor);

        return "Please got to the sign up page to complete your registration";
    }

    @Override
    public String companyAdminSignup(String email, String token){
        Company company = companyRepository.findByCompanyEmailAndSignupToken(email, token);
        if (company == null) {
            throw new CustomException("Invalid company. Contact Admin!");
        }
        company.setEnabled(true);
        company.setSignupToken(null);
        companyRepository.save(company);

        return "Please got to the sign up page to complete your registration";
    }

    @Override
    public String staffAdminSignup(String token) {
        User staffUser = userRepository.findByVerificationToken(token);
        if (staffUser == null) {
            throw new CustomException("Invalid user. Contact Admin!!");
        }
        staffUser.setEnabled(true);
        staffUser.setVerificationToken(null);
        userRepository.save(staffUser);

        return "Please go to the sign-up page to complete your registration";
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {


            String email = loginRequest.getEmail();
            User user = userRepository.findByEmail(email);
            Vendor vendor = vendorRepository.findByEmail(email);
            Admin admin = adminRepository.findByEmail(email);
            Company company = companyRepository.findByCompanyEmail(email);

            if (user == null && vendor == null && admin == null && company == null) {
                throw new CustomException("User with " + email + " does not exist");
            }
            if (vendor != null) {
                if (!vendor.getEnabled()) {
                    throw new CustomException("Vendor with " + email + " is not enabled");
                }
                if (vendor.getDeactivated()) {
                    throw new CustomException("Account has been deactivated! Contact Admin for support!");
                }
                if (passwordEncoder.matches(loginRequest.getPassword(), vendor.getPassword())) {
                    return performLogin(loginRequest, ROLE.VENDOR);
                }
            } else if (user != null) {
                if (!user.getEnabled()) {
                    throw new CustomException("Your account has not been enabled");
                }
                if (!user.getActive()) {
                    throw new CustomException("Your account has deactivated. Contact Admin for support!");
                }
                // User authentication logic
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    return performLogin(loginRequest, ROLE.COMPANY_STAFF);
                }

            } else if (company != null) {
                if (!company.getEnabled()) {
                    throw new CustomException("Your account has not been enabled");
                }
                if (company.getDeactivated()) {
                    throw new CustomException("Your account has been Deactivated. Contact Admin for support!");
                }
                // User authentication logic
                if (passwordEncoder.matches(loginRequest.getPassword(), company.getPassword())) {
                    return performLogin(loginRequest, ROLE.COMPANY_ADMIN);
                }

            } else {
                // Admin authentication logic
                if (passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
                    return performLogin(loginRequest, ROLE.SUPER_ADMIN);
                }
            }

        throw new CustomException("Incorrect Credentials!!!");
    }

    private LoginResponse performLogin(LoginRequest loginRequest, ROLE role) {
        String email = loginRequest.getEmail();
        String accessToken = jwtService.generateToken(createAuthentication(email, loginRequest.getPassword()));
        String refreshToken = jwtService.generateRefreshToken(createAuthentication(email, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(createAuthentication(email, loginRequest.getPassword()));

        return LoginResponse.builder()
                .email(email)
                .message(role + " logged in successfully!!!")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String forgotPassword(ResetEmail resetEmail) {
        String email = resetEmail.getEmail();

        User user = userRepository.findByEmail(email);
        Vendor vendor = vendorRepository.findByEmail(email);
        Company company = companyRepository.findByCompanyEmail(email);
        Admin admin = adminRepository.findByEmail(email);

        if (user == null && vendor == null && company == null && admin == null) {
            throw new CustomException("User with " + email + " does not exist");
        }

        // Generate and save the password reset token
        String resetToken = generateResetToken();

        if (user != null) {
            user.setVerificationToken(resetToken);
            userRepository.save(user);
        } else if (vendor != null) {
            vendor.setVerificationToken(resetToken);
            vendorRepository.save(vendor);
        } else if (admin !=null ){
            admin.setVerificationToken(resetToken);
        } else {
            company.setVerificationToken(resetToken);
            companyRepository.save(company);
        }
        sendPasswordResetEmail(email, resetToken);

        return "Password reset code has been sent to your email address!!!.";
    }

    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String resetToken = resetPasswordRequest.getResetToken();
        String newPassword = resetPasswordRequest.getNewPassword();
        String confirmNewPassword = resetPasswordRequest.getConfirmPassword();

        User user = userRepository.findByVerificationToken(resetToken);
        Vendor vendor = vendorRepository.findByVerificationToken(resetToken);
        Company company = companyRepository.findByVerificationToken(resetToken);

        if (user != null) {
            if(!newPassword.equals(confirmNewPassword)) {
                throw new CustomException("Password does not match");
            }
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            user.setVerificationToken(null);
            userRepository.save(user);
            return "User password reset successful.";
        } else if (vendor != null) {
            if(!newPassword.equals(confirmNewPassword)) {
                throw new CustomException("Password does not match");
            }
            String encodedPassword = passwordEncoder.encode(newPassword);
            vendor.setPassword(encodedPassword);
            vendor.setVerificationToken(null);
            vendorRepository.save(vendor);
            return "Vendor password reset successful.";
        } else if (company != null) {
            if(!newPassword.equals(confirmNewPassword)) {
                throw new CustomException("Password does not match");
            }
            String encodedPassword = passwordEncoder.encode(newPassword);
            company.setPassword(encodedPassword);
            company.setVerificationToken(null);
            companyRepository.save(company);
            return "Company password reset successfully.";
        } else {
            throw new CustomException("Invalid password reset token.");
        }
    }

    private void sendPasswordResetEmail(String email, String resetToken) {
        String emailSubject = "Password Reset Request";
        String emailBody = "Please use the following token to reset your password: " + resetToken;

        EmailDetails emailDetails = new EmailDetails(email, emailSubject, emailBody);
        // Use the email service to send the email
        emailService.sendEmail(emailDetails);
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
