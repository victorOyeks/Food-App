package com.example.foodapp.service;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.SalesReportDTO;
import com.example.foodapp.payloads.request.VendorRegistrationRequest;
import com.example.foodapp.payloads.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface VendorService {
    BusinessRegistrationResponse vendorSignup(VendorRegistrationRequest request);
    BusinessRegistrationResponse updateVendorProfile(String firstName, String lastName,
                                                     String phone, String businessName, String domainName,
                                                     String businessAddress, MultipartFile file) throws IOException;
    String changePassword(ChangePasswordRequest request);
    List<OrderDetailsResponse> viewAllOrdersToVendor(TimeFrame timeFrame);
    List<OrderDetailsResponse> viewAllProcessedOrdersToVendor(TimeFrame timeFrame);
    List<OrderDetailsResponse> viewAllPendingOrdersToVendor(TimeFrame timeFrame);
    //List<OrderDetailsResponse> viewAllLiveOrdersToVendor();
    AdminOrderResponse viewOrderByUser(String orderId);
    String changeDeliveryStatus(String orderId, DeliveryStatus newStatus);
    String changeStoreStatus(Boolean storeStatus);
    BusinessRegistrationResponse viewVendorProfile();
    VendorDashboardSummaryResponse getVendorSummary(TimeFrame timeFrame);
    List<SalesReportDTO> generateSalesReport(LocalDate startDate, LocalDate endDate, TimeFrame timeFrame);
    //OrderSummary calculateOrderSummary(List<OrderDetailsResponse> orders);
}
