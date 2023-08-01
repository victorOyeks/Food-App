package com.example.foodapp.service;

import com.example.foodapp.dto.request.*;
import com.example.foodapp.dto.response.LoginResponse;
import com.example.foodapp.dto.response.RegistrationResponse;
import com.example.foodapp.dto.response.UserResponse;

import java.io.IOException;

public interface UserService {
    UserResponse signup(RegistrationRequest request) throws IOException;
    String verifyAccount(String verificationToken);
    LoginResponse authenticate(LoginRequest loginRequest);
    String resetPassword(ResetPasswordRequest resetPasswordRequest);
    String forgotPassword(String email) throws IOException;
}