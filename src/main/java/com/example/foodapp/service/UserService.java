package com.example.foodapp.service;

import com.example.foodapp.payloads.request.*;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.ItemMenuReview;
import com.example.foodapp.entities.VendorReview;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    UserResponse signup(RegistrationRequest request) throws IOException;
    UserResponse updateUserProfile(String firstName, String lastName, String phone, MultipartFile profilePhoto) throws IOException;
    String changePassword(ChangePasswordRequest request);
    String verifyAccount(String verificationToken);
    LoginResponse authenticate(LoginRequest loginRequest);
    List<UserDashBoardResponse> userLandingPage();
    List<UserDashBoardResponse> userCompanyVendors();
    UserResponse viewUserProfile() throws IOException;
    String forgotPassword(String email) throws IOException;
    DetailsResponse getVendorDetails(String vendorId);
    VendorReviewResponse addRatingAndReviewByUser(VendorReview vendorReview, String vendorId, ReviewRequest reviewRequest);
    ItemMenuReviewResponse addRatingAndReviewToItemMenuByUser(ItemMenuReview itemMenuReview, String itemMenuId, ReviewRequest reviewRequest);
}