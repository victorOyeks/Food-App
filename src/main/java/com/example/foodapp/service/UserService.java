package com.example.foodapp.service;

import com.example.foodapp.dto.request.*;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.entities.Review;
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
    UserResponse viewUserProfile() throws IOException;
    String forgotPassword(String email) throws IOException;
    DetailsResponse getVendorDetails(String vendorId);
    ReviewResponse addRatingAndReviewByUser(Review review, String vendorId, ReviewRequest reviewRequest);
}