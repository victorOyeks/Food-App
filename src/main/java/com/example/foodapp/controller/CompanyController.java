package com.example.foodapp.controller;

import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.dto.request.ReviewRequest;
import com.example.foodapp.dto.request.StaffInvitation;
import com.example.foodapp.dto.response.ApiResponse;
import com.example.foodapp.dto.response.BusinessRegistrationResponse;
import com.example.foodapp.dto.response.CompanyResponse;
import com.example.foodapp.dto.response.ReviewResponse;
import com.example.foodapp.entities.Review;
import com.example.foodapp.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/company/")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("invite-staff")
    public ResponseEntity<ApiResponse<String>> inviteStaff(@RequestBody StaffInvitation staffInvitation) throws IOException {
        ApiResponse<String> apiResponse = new ApiResponse<>(companyService.inviteStaff(staffInvitation));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("update-profile")
    public ResponseEntity<ApiResponse<BusinessRegistrationResponse>> updateCompanyProfile(
            @RequestParam String companyName,
            @RequestParam String companyAddress,
            @RequestParam String phoneNumber,
            @RequestParam CompanySize companySize,
            @RequestParam(required = false) MultipartFile file
    ) throws IOException {
        ApiResponse<BusinessRegistrationResponse> response = new ApiResponse<>(companyService.updateCompanyProfile(companyName, companyAddress, phoneNumber, companySize, file));
        return ResponseEntity.ok(response);
    }

    @GetMapping("profile")
    public ResponseEntity<ApiResponse<CompanyResponse>> viewProfile () {
        ApiResponse<CompanyResponse> apiResponse = new ApiResponse<>(companyService.viewCompanyProfile());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("vendors/{vendorId}/company-reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(@PathVariable String vendorId,
                                                                 @RequestBody ReviewRequest reviewRequest, Review review) {
        ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(companyService.addRatingAndReviewByCompany(review, vendorId, reviewRequest));
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}