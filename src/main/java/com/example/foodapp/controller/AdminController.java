package com.example.foodapp.controller;

import com.example.foodapp.dto.request.CompanyInvitation;
import com.example.foodapp.dto.request.VendorInvitation;
import com.example.foodapp.dto.response.ApiResponse;
import com.example.foodapp.dto.response.UserResponse;
import com.example.foodapp.dto.response.DetailsResponse;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.exception.UserAlreadyExistException;
import com.example.foodapp.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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
    public ResponseEntity<ApiResponse<List<DetailsResponse>>> viewAllVendors() {
        List<DetailsResponse> vendorDetails = adminService.getAllVendorDetails();
        ApiResponse<List<DetailsResponse>> apiResponse = new ApiResponse<>(vendorDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/all-companies")
    public ResponseEntity<ApiResponse<List<DetailsResponse>>> viewAllCompanies() {
        List<DetailsResponse> vendorDetails = adminService.getAllCompanyDetails();
        ApiResponse<List<DetailsResponse>> apiResponse = new ApiResponse<>(vendorDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/all-users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllOnboardedUsers() {
        List<UserResponse> users = adminService.getAllOnboardedUsers();
        ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>(users);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
