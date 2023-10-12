package com.example.foodapp.service;

import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.exception.UserAlreadyExistException;
import com.example.foodapp.payloads.request.CompanyRegistrationRequest;
import com.example.foodapp.payloads.request.GraphReportDTO;
import com.example.foodapp.payloads.request.StaffInvitation;
import com.example.foodapp.payloads.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CompanyService {
    BusinessRegistrationResponse companySignup(CompanyRegistrationRequest request) throws IOException;
    String inviteStaff(StaffInvitation staffInvitation) throws IOException;
    String addVendor (String vendorId, String note) throws UserAlreadyExistException, IOException;
    List<DetailsResponse> getAllVendorDetails();
    List<OrderDetailsResponse> viewOrdersByCompanyStaff();
    OrderViewResponse viewOrderDetailsByCompany(String orderId);
    String removeVendor (String vendorId, String note) throws UserAlreadyExistException, IOException;
    String forgotPassword(String email) throws IOException;
    BusinessRegistrationResponse updateCompanyProfile(String companyName, String companyAddress,
                                                      String phoneNumber, CompanySize companySize, String domainName,
                                                      BigDecimal priceLimit, MultipartFile file) throws IOException;
    CompanyDetailsResponse getCompanyDashboard();
    List<GraphReportDTO> generateCompanySpendingReport(LocalDate startDate, LocalDate endDate, TimeFrame timeFrame);
    CompanyResponse viewCompanyProfile();
    UserResponse viewCompanyStaff(String staffId);
    List<UserResponse> getCompanyStaff();
    List<DetailsResponse> getCompanyVendors();
    //VendorReviewResponse addRatingAndReviewByCompany(VendorReview vendorReview, String vendorId, ReviewRequest reviewRequest);
    //ItemMenuReviewResponse addRatingAndReviewToItemMenuByCompany(ItemMenuReview itemMenuReview, String itemMenuId, ReviewRequest reviewRequest);
}
