package com.example.foodapp.controller;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.SalesReportDTO;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vendors/")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @GetMapping("all-orders")
    public ResponseEntity<ApiResponse<List<OrderDetailsResponse>>> viewAllOrdersToVendor(@RequestParam(required = false) TimeFrame timeFrame) {
        List<OrderDetailsResponse> orders = vendorService.viewAllOrdersToVendor(timeFrame);
        ApiResponse<List<OrderDetailsResponse>> apiResponse = new ApiResponse<>(orders);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("processed-orders")
    public ResponseEntity<ApiResponse<List<OrderDetailsResponse>>> viewAllProcessedOrdersToVendor(@RequestParam(required = false) TimeFrame timeFrame) {
        List<OrderDetailsResponse> orders = vendorService.viewAllProcessedOrdersToVendor(timeFrame);
        ApiResponse<List<OrderDetailsResponse>> apiResponse = new ApiResponse<>(orders);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("pending-orders")
    public ResponseEntity<ApiResponse<List<OrderDetailsResponse>>> viewAllPendingOrdersToVendor(@RequestParam(required = false) TimeFrame timeFrame) {
        List<OrderDetailsResponse> orders = vendorService.viewAllPendingOrdersToVendor(timeFrame);
        ApiResponse<List<OrderDetailsResponse>> apiResponse = new ApiResponse<>(orders);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /*
    @GetMapping("live-orders")
    public ResponseEntity<ApiResponse<List<OrderDetailsResponse>>> viewAllLiveOrdersToVendor() {
        List<OrderDetailsResponse> orders = vendorService.viewAllLiveOrdersToVendor();
        ApiResponse<List<OrderDetailsResponse>> apiResponse = new ApiResponse<>(orders);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
     */

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

    @PutMapping("change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>(vendorService.changePassword(changePasswordRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/{orderId}/delivery")
    public ResponseEntity<ApiResponse<String>> changeDeliveryStatus(@PathVariable String orderId, @RequestParam DeliveryStatus newStatus) {
        ApiResponse<String> apiResponse = new ApiResponse<>(vendorService.changeDeliveryStatus(orderId, newStatus));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/change-store-status")
    public ResponseEntity<ApiResponse<String>> changeStoreStatus(@RequestParam Boolean newStatus) {
        ApiResponse<String> apiResponse = new ApiResponse<>(vendorService.changeStoreStatus(newStatus));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/users/orders")
    public ResponseEntity<ApiResponse<AdminOrderResponse>> viewOrdersByUserOrCompany(@RequestParam String orderId) {
        AdminOrderResponse order = vendorService.viewOrderByUser(orderId);
        ApiResponse<AdminOrderResponse> apiResponse = new ApiResponse<>(order);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("profile")
    public ResponseEntity<ApiResponse<BusinessRegistrationResponse>> profile() {
        ApiResponse<BusinessRegistrationResponse> apiResponse = new ApiResponse<>(vendorService.viewVendorProfile());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("dashboard-summary")
    public ResponseEntity<ApiResponse<VendorDashboardSummaryResponse>> vendorDashboard(TimeFrame timeFrame) {
        ApiResponse<VendorDashboardSummaryResponse> apiResponse = new ApiResponse<>(vendorService.getVendorSummary(timeFrame));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

//    @GetMapping("/sales-report")
//    public ResponseEntity<List<SalesReportDTO>> getSalesReport(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//                                                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        List<SalesReportDTO> salesReport = vendorService.generateSalesReport(startDate, endDate);
//        return ResponseEntity.ok(salesReport);
//    }

    @GetMapping("/sales-report")
    public ResponseEntity<List<SalesReportDTO>> getSalesReport(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                               @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                               @RequestParam("timeFrame") TimeFrame timeFrame) {
        List<SalesReportDTO> salesReport = vendorService.generateSalesReport(startDate, endDate, timeFrame);
        return ResponseEntity.ok(salesReport);
    }
}
