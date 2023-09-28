package com.example.foodapp.controller;

import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.payloads.request.ReviewRequest;
import com.example.foodapp.payloads.request.StaffInvitation;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.ItemMenuReview;
import com.example.foodapp.entities.VendorReview;
import com.example.foodapp.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/company/")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("invite-staff")
    public ResponseEntity<ApiResponse<String>> inviteStaff(@RequestBody StaffInvitation staffInvitation) throws IOException {
        ApiResponse<String> apiResponse = new ApiResponse<>(companyService.inviteStaff(staffInvitation));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PostMapping("add-vendor")
    public ResponseEntity<ApiResponse<String>> addVendor (@RequestParam String vendorId, @RequestBody String note) throws IOException {
        ApiResponse<String> apiResponse = new ApiResponse<>(companyService.addVendor(vendorId, note));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("remove-vendor")
    public ResponseEntity<ApiResponse<String>> removeVendor (@RequestParam String vendorId, @RequestBody String note) throws IOException {
        ApiResponse<String> apiResponse = new ApiResponse<>(companyService.removeVendor(vendorId, note));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("update-profile")
    public ResponseEntity<ApiResponse<BusinessRegistrationResponse>> updateCompanyProfile(
            @RequestParam String companyName,
            @RequestParam String companyAddress,
            @RequestParam String phoneNumber,
            @RequestParam CompanySize companySize,
            @RequestParam String domainName,
            @RequestParam BigDecimal priceLimit,
            @RequestParam(required = false) MultipartFile file) throws IOException {
        ApiResponse<BusinessRegistrationResponse> response = new ApiResponse<>(
                companyService.updateCompanyProfile(companyName, companyAddress, phoneNumber, companySize, domainName, priceLimit, file));
        return ResponseEntity.ok(response);
    }

    @GetMapping("profile")
    public ResponseEntity<ApiResponse<CompanyResponse>> viewProfile () {
        ApiResponse<CompanyResponse> apiResponse = new ApiResponse<>(companyService.viewCompanyProfile());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/company-vendors")
    public ResponseEntity<ApiResponse<List<DetailsResponse>>> viewCompanyVendors(){
        List<DetailsResponse> vendorDetails = companyService.getCompanyVendors();
        ApiResponse<List<DetailsResponse>> apiResponse = new ApiResponse<>(vendorDetails);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    /*
    @PostMapping("vendors/{vendorId}/company-reviews")
    public ResponseEntity<ApiResponse<VendorReviewResponse>> addVendorReview(@PathVariable String vendorId,
                                                                       @RequestBody ReviewRequest reviewRequest, VendorReview vendorReview) {
        ApiResponse<VendorReviewResponse> apiResponse = new ApiResponse<>(companyService.addRatingAndReviewByCompany(vendorReview, vendorId, reviewRequest));
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("itemMenus/{itemMenusId}/company-reviews")
    public ResponseEntity<ApiResponse<ItemMenuReviewResponse>> addItemMenuReview(@PathVariable String itemMenusId,
                                                                                 @RequestBody ReviewRequest reviewRequest, ItemMenuReview itemMenuReview) {
        ApiResponse<ItemMenuReviewResponse> apiResponse = new ApiResponse<>(companyService.addRatingAndReviewToItemMenuByCompany(itemMenuReview, itemMenusId, reviewRequest));
        return  new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

     */
}