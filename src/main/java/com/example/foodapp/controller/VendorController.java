package com.example.foodapp.controller;

import com.example.foodapp.dto.request.ReviewRequest;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.entities.Review;
import com.example.foodapp.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vendors/")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @GetMapping("orders")
    public ResponseEntity<OrderViewResponse> viewAllOrdersToVendor() {
        List<OrderResponse> orders = vendorService.viewAllOrdersToVendor();
        OrderSummary orderSummary = vendorService.calculateOrderSummary(orders);
        OrderViewResponse orderViewResponse = new OrderViewResponse(orders, orderSummary);
        return ResponseEntity.ok(orderViewResponse);
    }

    @PutMapping("update-profile")
    public ResponseEntity<ApiResponse<BusinessRegistrationResponse>> updateVendorProfile(@RequestParam String firstName,
                                                                                         @RequestParam String lastName,
                                                                                         @RequestParam String phone,
                                                                                         @RequestParam String businessName,
                                                                                         @RequestParam String domainName,
                                                                                         @RequestParam String businessAddress,
                                                                                         @RequestParam MultipartFile file) throws IOException {
        ApiResponse<BusinessRegistrationResponse> apiResponse = new ApiResponse<>(
                vendorService.updateVendorProfile(firstName, lastName, phone, businessName, domainName,
                        businessAddress, file));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("profile")
    public ResponseEntity<ApiResponse<BusinessRegistrationResponse>> profile() {
        ApiResponse<BusinessRegistrationResponse> apiResponse = new ApiResponse<>(vendorService.viewVendorProfile());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("{vendorId}/add-reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(@PathVariable String vendorId,
                                                                               @RequestBody ReviewRequest reviewRequest, Review review) {
        ApiResponse<ReviewResponse> apiResponse = new ApiResponse<>(vendorService.addRatingAndReview(review, vendorId, reviewRequest));
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
