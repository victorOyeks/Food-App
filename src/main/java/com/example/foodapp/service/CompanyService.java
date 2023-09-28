package com.example.foodapp.service;

import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.exception.UserAlreadyExistException;
import com.example.foodapp.payloads.request.CompanyRegistrationRequest;
import com.example.foodapp.payloads.request.ReviewRequest;
import com.example.foodapp.payloads.request.StaffInvitation;
import com.example.foodapp.payloads.request.VendorInvitation;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.ItemMenuReview;
import com.example.foodapp.entities.VendorReview;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface CompanyService {
    BusinessRegistrationResponse companySignup(CompanyRegistrationRequest request) throws IOException;
    String inviteStaff(StaffInvitation staffInvitation) throws IOException;
    String addVendor (String vendorId, String note) throws UserAlreadyExistException, IOException;
    String removeVendor (String vendorId, String note) throws UserAlreadyExistException, IOException;
    String forgotPassword(String email) throws IOException;
    BusinessRegistrationResponse updateCompanyProfile(String companyName, String companyAddress,
                                                      String phoneNumber, CompanySize companySize, String domainName,
                                                      BigDecimal priceLimit, MultipartFile file) throws IOException;
    CompanyResponse viewCompanyProfile();
    List<DetailsResponse> getCompanyVendors();
    //VendorReviewResponse addRatingAndReviewByCompany(VendorReview vendorReview, String vendorId, ReviewRequest reviewRequest);
    //ItemMenuReviewResponse addRatingAndReviewToItemMenuByCompany(ItemMenuReview itemMenuReview, String itemMenuId, ReviewRequest reviewRequest);
}
