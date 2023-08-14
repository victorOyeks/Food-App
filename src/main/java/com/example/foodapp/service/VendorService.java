package com.example.foodapp.service;

import com.example.foodapp.dto.request.ReviewRequest;
import com.example.foodapp.dto.request.VendorRegistrationRequest;
import com.example.foodapp.dto.response.BusinessRegistrationResponse;
import com.example.foodapp.dto.response.OrderResponse;
import com.example.foodapp.dto.response.OrderSummary;
import com.example.foodapp.dto.response.ReviewResponse;
import com.example.foodapp.entities.Review;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VendorService {
    BusinessRegistrationResponse vendorSignup(VendorRegistrationRequest request);
    BusinessRegistrationResponse updateVendorProfile(String firstName, String lastName,
                                                     String phone, String businessName, String domainName,
                                                     String businessAddress, MultipartFile file) throws IOException;
    List<OrderResponse> viewAllOrdersToVendor();
    BusinessRegistrationResponse viewVendorProfile();
    OrderSummary calculateOrderSummary(List<OrderResponse> orders);
    ReviewResponse addRatingAndReview(Review review, String vendorId, ReviewRequest reviewRequest);
}
