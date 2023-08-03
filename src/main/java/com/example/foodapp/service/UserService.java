package com.example.foodapp.service;

import com.example.foodapp.dto.request.*;
import com.example.foodapp.dto.response.LoginResponse;
import com.example.foodapp.dto.response.RegistrationResponse;
import com.example.foodapp.dto.response.UserDashBoardResponse;
import com.example.foodapp.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponse signup(RegistrationRequest request) throws IOException;
    UserResponse updateUserProfile(String firstName, String lastName, String phone, MultipartFile profilePhoto) throws IOException;
    String changePassword(ChangePasswordRequest request);
    String verifyAccount(String verificationToken);
    LoginResponse authenticate(LoginRequest loginRequest);
    List<UserDashBoardResponse> getUserDashBoard();
    String forgotPassword(String email) throws IOException;
}