package com.example.foodapp.service;

import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.payloads.request.CompanyRegistrationRequest;
import com.example.foodapp.payloads.request.ReviewRequest;
import com.example.foodapp.payloads.request.StaffInvitation;
import com.example.foodapp.payloads.response.BusinessRegistrationResponse;
import com.example.foodapp.payloads.response.CompanyResponse;
import com.example.foodapp.payloads.response.ReviewResponse;
import com.example.foodapp.entities.Review;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CompanyService {
    BusinessRegistrationResponse companySignup(CompanyRegistrationRequest request) throws IOException;
    String inviteStaff(StaffInvitation staffInvitation) throws IOException;
    String forgotPassword(String email) throws IOException;
    BusinessRegistrationResponse updateCompanyProfile(String companyName, String companyAddress,
                                                      String phoneNumber, CompanySize companySize, MultipartFile file) throws IOException;
    CompanyResponse viewCompanyProfile();
    ReviewResponse addRatingAndReviewByCompany(Review review, String vendorId, ReviewRequest reviewRequest);
}
