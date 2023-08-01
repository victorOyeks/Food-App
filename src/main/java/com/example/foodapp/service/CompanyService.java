package com.example.foodapp.service;

import com.example.foodapp.dto.request.CompanyRegistrationRequest;
import com.example.foodapp.dto.request.StaffInvitation;
import com.example.foodapp.dto.response.BusinessRegistrationResponse;

import java.io.IOException;

public interface CompanyService {
    BusinessRegistrationResponse companySignup(CompanyRegistrationRequest request) throws IOException;
    String inviteStaff(StaffInvitation staffInvitation) throws IOException;
    String forgotPassword(String email) throws IOException;
}
