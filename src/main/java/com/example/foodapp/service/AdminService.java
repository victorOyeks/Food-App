package com.example.foodapp.service;

import com.example.foodapp.dto.request.CompanyInvitation;
import com.example.foodapp.dto.request.VendorInvitation;
import com.example.foodapp.dto.response.UserResponse;
import com.example.foodapp.dto.response.DetailsResponse;
import com.example.foodapp.exception.UserAlreadyExistException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.List;

public interface AdminService {

    String inviteVendor(VendorInvitation vendorInvitation) throws UserAlreadyExistException, IOException;
    String inviteCompany(CompanyInvitation companyInvitation) throws IOException;
    void deactivateUser(String userId) throws IOException;
    void reactivateUser(String userId) throws IOException;
    void deactivateVendor (String vendorId) throws IOException;
    void reactivateVendor(String vendorId) throws IOException;
    List<DetailsResponse> getAllVendorDetails();
    List<DetailsResponse> getAllCompanyDetails();
    List<UserResponse> getAllOnboardedUsers();
}
