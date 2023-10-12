package com.example.foodapp.service;

import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.CompanyInvitation;
import com.example.foodapp.payloads.request.VendorInvitation;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.exception.UserAlreadyExistException;

import java.io.IOException;
import java.util.List;

public interface AdminService {

    String changePassword(ChangePasswordRequest request);
    String inviteVendor(VendorInvitation vendorInvitation) throws UserAlreadyExistException, IOException;
    String inviteCompany(CompanyInvitation companyInvitation) throws IOException;
    void deactivateUser(String userId) throws IOException;
    void reactivateUser(String userId) throws IOException;
    void deactivateVendor (String vendorId) throws IOException;
    void reactivateVendor(String vendorId) throws IOException;
    List<DetailsResponse> getAllVendorDetails() throws IOException;
    BusinessRegistrationResponse getVendor(String vendorId);
//    List<CompanyResponse> getAllCompanyDetails();
    List<CompanyResponse> getAllCompanyDetails(boolean active);
    List<UserResponse> getAllOnboardedUsers();
    List<CategoryResponse> getAllItemCategory();
    List<CustomerResponse> getAllCustomers();
    List<ItemMenuInfoResponse> getAllItemMenus();
    List<ItemMenusInCategoriesResponse> getAllItemMenusInAllCategories(String vendorId);
    CategoryResponse getItemMenusInCategory(String vendorId, String categoryId);
//    List<OrderDetailsResponse> viewAllOrders();
    List<ItemNamesResponse> getAllItemsInCategory(String vendorId);
    List<OrderDetailsResponse> viewOrdersByTimeFrame(TimeFrame timeFrame);
    //List<AdminOrderResponse> viewOrderByUserOrCompany(String userIdOrCompanyId);
    AdminOrderResponse viewOrderByUser(String orderId, String userId);
}
