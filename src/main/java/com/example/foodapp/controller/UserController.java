package com.example.foodapp.controller;

import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.ReviewRequest;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.ItemMenuReview;
import com.example.foodapp.entities.VendorReview;
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

    @GetMapping("company-vendors")
    public ResponseEntity<ApiResponse<List<UserDashBoardResponse>>> userCompanyVendors() {
        ApiResponse<List<UserDashBoardResponse>> apiResponse = new ApiResponse<>(userService.userCompanyVendors());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("profile")
    public ResponseEntity<ApiResponse<UserResponse>> profile() throws IOException {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(userService.viewUserProfile());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("vendors")
    public ResponseEntity<ApiResponse<DetailsResponse>> vendorDetails(@RequestParam String vendorId) {
        ApiResponse<DetailsResponse> apiResponse = new ApiResponse<>(userService.getVendorDetails(vendorId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("update-profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestParam String firstName,
                                                                   @RequestParam String lastName,
                                                                   @RequestParam String phone,
                                                                   @RequestParam String position,
                                                                   @RequestParam MultipartFile profilePhoto) throws IOException {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(userService.updateUserProfile(firstName, lastName, phone, position, profilePhoto));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>(userService.changePassword(changePasswordRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("vendors/user-reviews")
    public ResponseEntity<ApiResponse<VendorReviewResponse>> addVendorReview(@RequestParam String vendorId,
                                                                       @RequestBody ReviewRequest reviewRequest, VendorReview vendorReview) {
        ApiResponse<VendorReviewResponse> apiResponse = new ApiResponse<>(userService.addRatingAndReviewByUser(vendorReview, vendorId, reviewRequest));
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

        @PostMapping("itemMenus/{itemMenusId}/user-reviews")
    public ResponseEntity<ApiResponse<ItemMenuReviewResponse>> addItemMenuReview(@PathVariable String itemMenusId,
                                                                       @RequestBody ReviewRequest reviewRequest, ItemMenuReview itemMenuReview) {
        ApiResponse<ItemMenuReviewResponse> apiResponse = new ApiResponse<>(userService.addRatingAndReviewToItemMenuByUser(itemMenuReview, itemMenusId, reviewRequest));
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}