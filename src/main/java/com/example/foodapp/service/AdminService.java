package com.example.foodapp.service;

import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.dto.request.CompanyInvitation;
import com.example.foodapp.dto.request.VendorInvitation;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.exception.UserAlreadyExistException;

import java.io.IOException;
import java.util.List;

public interface AdminService {

    String inviteVendor(VendorInvitation vendorInvitation) throws UserAlreadyExistException, IOException;
    String inviteCompany(CompanyInvitation companyInvitation) throws IOException;
    void deactivateUser(String userId) throws IOException;
    void reactivateUser(String userId) throws IOException;
    void deactivateVendor (String vendorId) throws IOException;
    void reactivateVendor(String vendorId) throws IOException;
    List<DetailsResponse> getAllVendorDetails() throws IOException;
    BusinessRegistrationResponse getVendor(String vendorId);
    List<DetailsResponse> getAllCompanyDetails();
    List<UserResponse> getAllOnboardedUsers();
    List<CategoryResponse> getAllItemCategory();
    List<CustomerResponse> getAllCustomers();
    List<ItemMenuInfoResponse> getAllItemMenus();
    List<ItemMenusInCategoriesResponse> getAllItemMenusInAllCategories(String vendorId);
    CategoryResponse getItemMenusInCategory(String vendorId, String categoryId);
//    List<OrderDetailsResponse> viewAllOrders();

    List<OrderDetailsResponse> viewOrdersByTimeFrame(TimeFrame timeFrame);
    List<AdminOrderResponse> viewAllOrdersByUserOrCompany(String userIdOrCompanyId);
}
