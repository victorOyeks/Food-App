package com.example.foodapp.service;

import com.example.foodapp.dto.request.LoginRequest;
import com.example.foodapp.dto.request.ResetEmail;
import com.example.foodapp.dto.request.ResetPasswordRequest;
import com.example.foodapp.dto.response.LoginResponse;

import java.io.IOException;

public interface AuthService {

    String vendorAdminSignup(String email, String token) throws IOException;
    String companyAdminSignup(String email, String token) throws IOException;
    String staffAdminSignup(String token);
    LoginResponse authenticate(LoginRequest loginRequest) throws IOException;
    String resetPassword(ResetPasswordRequest resetPasswordRequest);
    String forgotPassword(ResetEmail resetEmail);
}
