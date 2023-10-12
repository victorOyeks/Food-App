package com.example.foodapp.controller;

import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.CompanyInvitation;
import com.example.foodapp.payloads.request.VendorInvitation;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.exception.UserAlreadyExistException;
import com.example.foodapp.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @PutMapping("change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>(adminService.changePassword(changePasswordRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("invite-vendor")
    public ResponseEntity<ApiResponse<String>> inviteVendor(@RequestBody VendorInvitation vendorInvitation) throws UserAlreadyExistException, IOException {
        ApiResponse<String> apiResponse = new ApiResponse<>(adminService.inviteVendor(vendorInvitation));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("invite-company")
    public ResponseEntity<ApiResponse<String>> inviteCompany (@RequestBody CompanyInvitation companyInvitation) throws IOException {
        ApiResponse<String> apiResponse = new ApiResponse<>(adminService.inviteCompany(companyInvitation));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("deactivate-vendor")
    public ResponseEntity<ApiResponse<String>> deactivateVendor(@RequestParam String vendorId) {
        try {
            adminService.deactivateVendor(vendorId);
            ApiResponse<String> apiResponse = new ApiResponse<>("Vendor deactivated successfully!");
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (CustomException | IOException e) {
            ApiResponse<String> apiResponse = new ApiResponse<>("Vendor deactivation failed: " + e.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("reactivate-vendor")
    public ResponseEntity<ApiResponse<String>> reactivateVendor (@RequestParam String vendorId) throws IOException {
        adminService.reactivateVendor(vendorId);
        ApiResponse<String> apiResponse = new ApiResponse<>("Vendor reactivated successfully!");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("deactivate-user")
    public ResponseEntity<ApiResponse<String>> deactivateUser (@RequestParam String userId) throws IOException {
        adminService.deactivateUser(userId);
        ApiResponse<String> apiResponse = new ApiResponse<>("User deactivated successfully!");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("reactivate-user")
    public ResponseEntity<ApiResponse<String>> reactivateUser (@RequestParam String userId) throws IOException {
        adminService.reactivateUser(userId);
        ApiResponse<String> apiResponse = new ApiResponse<>("User reactivated successfully!");
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/all-vendors")
    public ResponseEntity<ApiResponse<Page<DetailsResponse>>> viewAllVendors(@RequestParam (defaultValue = "0") int page,
                                                                             @RequestParam (defaultValue = "10") int size) throws IOException {
        Page<DetailsResponse> vendorDetails = adminService.getAllVendorDetails(page, size);
        ApiResponse<Page<DetailsResponse>> apiResponse = new ApiResponse<>(vendorDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("vendors/{vendorId}")
    public ResponseEntity<ApiResponse<BusinessRegistrationResponse>> viewVendor(@PathVariable String vendorId) {
        BusinessRegistrationResponse vendorDetails = adminService.getVendor(vendorId);
        ApiResponse<BusinessRegistrationResponse> apiResponse = new ApiResponse<>(vendorDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/all-companies")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> viewAllCompanies(@RequestParam("status") String status) {
        List<CompanyResponse> companyDetails;

        if ("active".equalsIgnoreCase(status)) {
            companyDetails = adminService.getAllCompanyDetails(true); // Pass 'true' for active companies
        } else if ("inactive".equalsIgnoreCase(status)) {
            companyDetails = adminService.getAllCompanyDetails(false); // Pass 'false' for inactive companies
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Shuffle the response if needed
        Collections.shuffle(companyDetails);

        ApiResponse<List<CompanyResponse>> apiResponse = new ApiResponse<>(companyDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

   /* @GetMapping("/all-users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllOnboardedUsers() {
        List<UserResponse> users = adminService.getAllOnboardedUsers();
        ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>(users);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    */

    @GetMapping("/all-users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllOnboardedUsers(@RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {

        Page<UserResponse> userPage = adminService.getAllOnboardedUsers(page, size);
        ApiResponse<Page<UserResponse>> apiResponse = new ApiResponse<>(userPage);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/all-categories")
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        ApiResponse<Page<CategoryResponse>> apiResponse = new ApiResponse<>(adminService.getAllItemCategory(page, size));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/all-customers")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers() {
        List<CustomerResponse> customers = adminService.getAllCustomers();
        ApiResponse<List<CustomerResponse>> apiResponse = new ApiResponse<>(customers);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/menu-list")
    public ResponseEntity<ApiResponse<List<ItemMenuInfoResponse>>> getAllItemMenus() {
        List<ItemMenuInfoResponse> itemMenus = adminService.getAllItemMenus();
        ApiResponse<List<ItemMenuInfoResponse>> apiResponseList = new ApiResponse<>(itemMenus);
        return new ResponseEntity<>(apiResponseList, HttpStatus.OK);
    }

    //TODO: dont delete the controller below
    /*@GetMapping("/all-orders")
    public ResponseEntity<ApiResponse<List<OrderDetailsResponse>>> viewAllOrders() {
        List<OrderDetailsResponse> orderDetailsResponses = adminService.viewAllOrders();
        ApiResponse<List<OrderDetailsResponse>> apiResponse = new ApiResponse<>(orderDetailsResponses);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }*/

    @GetMapping("/all-orders")
    public ResponseEntity<ApiResponse<List<OrderDetailsResponse>>> viewOrdersByTimeFrame(
            @RequestParam(required = false) TimeFrame timeFrame) {
        List<OrderDetailsResponse> orderDetailsResponses = adminService.viewOrdersByTimeFrame(timeFrame);
        ApiResponse<List<OrderDetailsResponse>> apiResponse = new ApiResponse<>(orderDetailsResponses);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("/items-all-categories")
    public ResponseEntity<ApiResponse<List<ItemMenusInCategoriesResponse>>> viewAllItemsInCategories (@RequestParam String vendorId) {
        List<ItemMenusInCategoriesResponse> allItems = adminService.getAllItemMenusInAllCategories(vendorId);
        ApiResponse<List<ItemMenusInCategoriesResponse>> apiResponse = new ApiResponse<>(allItems);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/items-all-items-categories")
    public ResponseEntity<ApiResponse<List<ItemNamesResponse>>> getAllItemsInCategory (@RequestParam String vendorId) {
        List<ItemNamesResponse> allItems = adminService.getAllItemsInCategory(vendorId);
        ApiResponse<List<ItemNamesResponse>> apiResponse = new ApiResponse<>(allItems);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/items-per-categories")
    public ResponseEntity<ApiResponse<CategoryResponse>> viewAllItemsInCategory (@RequestParam String vendorId, @RequestParam String categoryId) {
        CategoryResponse allItems = adminService.getItemMenusInCategory(vendorId, categoryId);
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>(allItems);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/users/orders")
    public ResponseEntity<ApiResponse<AdminOrderResponse>> viewOrdersByUserOrCompany(@RequestParam String orderId,
                                                                                     @RequestParam String userId) {
        AdminOrderResponse order = adminService.viewOrderByUser(orderId, userId);
        ApiResponse<AdminOrderResponse> apiResponse = new ApiResponse<>(order);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
