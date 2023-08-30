package com.example.foodapp.service;

import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.VendorRegistrationRequest;
import com.example.foodapp.payloads.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VendorService {
    BusinessRegistrationResponse vendorSignup(VendorRegistrationRequest request);
    BusinessRegistrationResponse updateVendorProfile(String firstName, String lastName,
                                                     String phone, String businessName, String domainName,
                                                     String businessAddress, MultipartFile file) throws IOException;
    String changePassword(ChangePasswordRequest request);
    List<OrderDetailsResponse> viewAllOrdersToVendor(TimeFrame timeFrame);
    AdminOrderResponse viewOrderByUserOrCompany(String orderId, String userIdOrCompanyId);
    BusinessRegistrationResponse viewVendorProfile();
    //OrderSummary calculateOrderSummary(List<OrderDetailsResponse> orders);
}
