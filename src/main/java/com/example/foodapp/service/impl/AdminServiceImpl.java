package com.example.foodapp.service.impl;

import com.example.foodapp.constant.ROLE;
import com.example.foodapp.dto.request.CompanyInvitation;
import com.example.foodapp.dto.request.EmailDetails;
import com.example.foodapp.dto.request.VendorInvitation;
import com.example.foodapp.dto.response.UserResponse;
import com.example.foodapp.dto.response.DetailsResponse;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.exception.UserAlreadyExistException;
import com.example.foodapp.repository.CompanyRepository;
import com.example.foodapp.repository.UserRepository;
import com.example.foodapp.repository.VendorRepository;
import com.example.foodapp.service.AdminService;
import com.example.foodapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final EmailService emailService;
    private final CompanyRepository companyRepository;

    @Override
    public String inviteVendor(VendorInvitation vendorInvitation) throws UserAlreadyExistException, IOException {
        String vendorEmail = vendorInvitation.getVendorEmail();
        String note = vendorInvitation.getNote();

        // Check if the vendor already exists
        boolean existingUserInUser = userRepository.existsByEmail(vendorEmail);
        if (existingUserInUser) {
            throw new CustomException("User with " + vendorInvitation.getVendorEmail() + " already exist");
        }

        boolean existingVendorInVendor = vendorRepository.existsByEmail(vendorEmail);
        if(existingVendorInVendor) {
            throw new CustomException("User with " + vendorInvitation.getVendorEmail() + " already exist");
        }

        boolean existingCompany = companyRepository.existsByCompanyEmail(vendorEmail);
        if(existingCompany) {
            throw new CustomException("User with " + vendorInvitation.getVendorEmail() + " already exist");
        }

        // Generate a signup token for the vendor
        String signupToken = generateSignupToken();

        // Save the vendor with the signup token
        Vendor vendor = new Vendor();
        vendor.setEmail(vendorEmail);
        vendor.setRole(ROLE.VENDOR);
        vendor.setEnabled(true);
        vendor.setDeactivated(false);
        vendor.setSignupToken(signupToken);
        vendorRepository.save(vendor);

        String invitationLink = "http://localhost:9191/api/auth/vendor-signup?email=" + URLEncoder.encode(vendorEmail, StandardCharsets.UTF_8) + "&token=" + signupToken;
        String subject = "Invitation to Sign Up";
        String messageBody = "Dear Vendor,\n\nYou have been invited to sign up on our platform. Please click the link below to complete your registration:\n\n" + invitationLink + "\n\nNote from the admin: " + note;
        EmailDetails emailDetails = new EmailDetails(vendorEmail, subject, messageBody);

        // Send the invitation email
        emailService.sendEmail(emailDetails);

        return "Vendor onboarded successfully. Email sent to vendor to complete registration";
    }

    @Override
    public String inviteCompany(CompanyInvitation companyInvitation) throws IOException {
        String companyEmail = companyInvitation.getCompanyEmail();
        String note = companyInvitation.getNote();

        // Check if the vendor already exists
        boolean existingUserInUser = userRepository.existsByEmail(companyEmail);
        if (existingUserInUser) {
            throw new CustomException("User with " + companyEmail + " already exists");
        }

        boolean existingVendorInVendor = vendorRepository.existsByEmail(companyEmail);
        if(existingVendorInVendor) {
            throw new CustomException("User with " + companyEmail + " already exists");
        }
        boolean existingCompanyInCompany = companyRepository.existsByCompanyEmail(companyEmail);
        if(existingCompanyInCompany) {
            throw new CustomException("User with " + companyEmail + " already exists");
        }

        // Generate a signup token for the vendor
        String signupToken = generateSignupToken();

        // Save the vendor with the signup token
        Company company = new Company();
        company.setCompanyEmail(companyEmail);
        company.setRole(ROLE.COMPANY_ADMIN);
        company.setEnabled(true);
        company.setDeactivated(false);
        company.setSignupToken(signupToken);
        companyRepository.save(company);

        String invitationLink = "http://localhost:9191/api/auth/company-signup?email=" + URLEncoder.encode(companyEmail, StandardCharsets.UTF_8) + "&token=" + signupToken;
        String subject = "Invitation to Sign Up";
        String messageBody = "Dear Company, \n\nYou have been invited to sign up on our platform. Please click the link below to complete your registration:\n\n" + invitationLink + "\n\nNote from the admin: " + note;
        EmailDetails emailDetails = new EmailDetails(companyEmail, subject, messageBody);

        // Send the invitation email
        emailService.sendEmail(emailDetails);

        return  "Company with " + companyEmail + " onboarded successfully. Email sent to company to complete registration";
    }

    @Override
    public void deactivateUser(String userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException("User not found with id: " + userId));
        if (user != null) {
            user.setLocked(true);
            userRepository.save(user);

            String subject = "Account Deactivated!";
            String messageBody = "Dear user, \n\n You account has been deactivated! Contact support for more information. Thank you";
            EmailDetails emailDetails = new EmailDetails(user.getEmail(), subject, messageBody);

            // Send the invitation email
            emailService.sendEmail(emailDetails);

        }
    }

    @Override
    public void reactivateUser(String userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException("User not found with id: " +userId));
        if (user != null) {
            user.setLocked(false);
            userRepository.save(user);

            String subject = "Account Reactivated!";
            String messageBody = "Dear user, \n\n You account has been reactivated! Regards.";
            EmailDetails emailDetails = new EmailDetails(user.getEmail(), subject, messageBody);

            // Send the invitation email
            emailService.sendEmail(emailDetails);

        }
    }

    @Override
    public void deactivateVendor(String userId) throws CustomException, IOException {
        Vendor vendor = vendorRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Vendor not found with id: " +userId));

        vendor.setDeactivated(true);
        vendorRepository.save(vendor);

        System.out.println("sending email to " + vendor);
        String subject = "Account Deactivated!";
        String messageBody = "Dear user, \n\nYour account has been deactivated! Contact support for more information. Thank you.";
        EmailDetails emailDetails = new EmailDetails(vendor.getEmail(), subject, messageBody);

        // Send the deactivation email
        emailService.sendEmail(emailDetails);

    }

    @Override
    public void reactivateVendor(String vendorId) throws IOException {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(()-> new CustomException("Vendor not found with id: " +vendorId));
        if(vendor != null) {
            vendor.setDeactivated(false);
            vendorRepository.save(vendor);

            String subject = "Account Deactivated!";
            String messageBody = "Dear vendor, \n\n You account has been reactivated! Regards.";
            EmailDetails emailDetails = new EmailDetails(vendor.getEmail(), subject, messageBody);

            // Send the invitation email
            emailService.sendEmail(emailDetails);
        }
    }

    @Override
    public List<DetailsResponse> getAllVendorDetails() {
        List<DetailsResponse> detailsResponses = new ArrayList<>();
        List<Vendor> vendors = vendorRepository.findAll();

        for (Vendor vendor : vendors) {
            DetailsResponse detailsResponse = new DetailsResponse();
            detailsResponse.setId(vendor.getId());
            detailsResponse.setBusinessName(vendor.getBusinessName());
            detailsResponse.setAddress(vendor.getBusinessAddress());
            detailsResponse.setContactNumber(vendor.getPhone());

            detailsResponses.add(detailsResponse);
        }
        return detailsResponses;
    }

    @Override
    public List<DetailsResponse> getAllCompanyDetails() {
        List<DetailsResponse> detailsResponses = new ArrayList<>();
        List<Company> companies = companyRepository.findAll();

        for (Company company : companies) {
            DetailsResponse detailsResponse = new DetailsResponse();
            detailsResponse.setId(company.getId());
            detailsResponse.setBusinessName(company.getCompanyName());
            detailsResponse.setAddress(company.getCompanyAddress());
            detailsResponse.setContactNumber(company.getPhoneNumber());

            detailsResponses.add(detailsResponse);
        }

        return detailsResponses;
    }

    public List<UserResponse> getAllOnboardedUsers() {
        List<UserResponse> userResponses = new ArrayList<>();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getRole() != ROLE.SUPER_ADMIN) {
                UserResponse userResponse = new UserResponse();
                userResponse.setUserId(user.getId());
                userResponse.setFirstName(user.getFirstName());
                userResponse.setLastName(user.getLastName());
                userResponse.setEmail(user.getEmail());

                userResponses.add(userResponse);
            }
        }
        return userResponses;
    }

    private String generateSignupToken() {
        // Generate a unique signup token for vendor
        UUID signupToken = UUID.randomUUID();
        return signupToken.toString();
    }
}
