package com.example.foodapp.controller;

import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.ReviewRequest;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.Review;
import com.example.foodapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("dashboard")
    public ResponseEntity<ApiResponse<List<UserDashBoardResponse>>> dashboard() {
        ApiResponse<List<UserDashBoardResponse>> apiResponse = new ApiResponse<>(userService.getUserDashBoard());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("profile")
    public ResponseEntity<ApiResponse<UserResponse>> profile() throws IOException {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(userService.viewUserProfile());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("vendors/{vendorId}")
    public ResponseEntity<ApiResponse<DetailsResponse>> vendorDetails(@PathVariable String vendorId) {
        ApiResponse<DetailsResponse> apiResponse = new ApiResponse<>(userService.getVendorDetails(vendorId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("update-profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestParam String firstName,
                                                                   @RequestParam String lastName,
                                                                   @RequestParam String phone,
                                                                   @RequestParam MultipartFile profilePhoto) throws IOException {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(userService.updateUserProfile(firstName, lastName, phone, profilePhoto));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>(userService.changePassword(changePasswordRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("vendors/{vendorId}/user-reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(@PathVariable String vendorId,
                                                                 @RequestBody ReviewRequest reviewRequest, Review review) {
        ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(userService.addRatingAndReviewByUser(review, vendorId, reviewRequest));
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}