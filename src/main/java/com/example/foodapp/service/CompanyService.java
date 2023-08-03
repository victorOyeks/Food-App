package com.example.foodapp.service;

import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.dto.request.CompanyRegistrationRequest;
import com.example.foodapp.dto.request.StaffInvitation;
import com.example.foodapp.dto.response.BusinessRegistrationResponse;
import com.example.foodapp.dto.response.CompanyResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CompanyService {
    BusinessRegistrationResponse companySignup(CompanyRegistrationRequest request) throws IOException;
    String inviteStaff(StaffInvitation staffInvitation) throws IOException;
    String forgotPassword(String email) throws IOException;
    BusinessRegistrationResponse updateCompanyProfile(String companyName, String companyAddress,
                                                      String phoneNumber, CompanySize companySize, MultipartFile file) throws IOException;
    CompanyResponse viewCompanyProfile();
}
