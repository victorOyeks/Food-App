package com.example.foodapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.constant.ROLE;
import com.example.foodapp.dto.request.CompanyRegistrationRequest;
import com.example.foodapp.dto.request.EmailDetails;
import com.example.foodapp.dto.request.ReviewRequest;
import com.example.foodapp.dto.request.StaffInvitation;
import com.example.foodapp.dto.response.BusinessRegistrationResponse;
import com.example.foodapp.dto.response.CompanyResponse;
import com.example.foodapp.dto.response.ReviewResponse;
import com.example.foodapp.entities.Company;
import com.example.foodapp.entities.Review;
import com.example.foodapp.entities.User;
import com.example.foodapp.entities.Vendor;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.*;
import com.example.foodapp.service.CompanyService;
import com.example.foodapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final EmailService emailService;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;
    private final Cloudinary cloudinary;
    private final ReviewRepository reviewRepository;

    @Override
    public BusinessRegistrationResponse companySignup(CompanyRegistrationRequest request) throws IOException {

        String email = request.getCompanyEmail();

        boolean isExistingCompany = companyRepository.existsByCompanyEmail(email);
        boolean isExistingUser = userRepository.existsByEmail(email);
        boolean isExistingVendor = vendorRepository.existsByEmail(email);
        boolean isExistingAdmin = adminRepository.existsByEmail(email);

        if (!isExistingCompany) {
            throw new CustomException("Contact Admin to get signed up!!!");
        }
        if(isExistingUser) {
            throw new CustomException("User with " + email + " already exist!");
        }
        if (isExistingVendor) {
            throw new CustomException("Vendor already exist with the email: " + email);
        }
        if (isExistingAdmin) {
            throw new CustomException("USer already exist with the email: " + email);
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("Password does not match");
        }

        Company existingCompany = companyRepository.findByCompanyEmail(request.getCompanyEmail());

        if (!existingCompany.getEnabled()) {
            throw new CustomException("Invalid Company. Contact Admin");
        }
        if(existingCompany.getPassword() != null) {
            throw new CustomException("User already exist!!!");
        }

//        if(existingCompany.getSignupToken() != null) {
//            throw new CustomException("Your account has not been verified!!!");
//        }

        String verificationToken = generateToken();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        existingCompany.setPhoneNumber(request.getPhoneNumber());
        existingCompany.setCompanyName(request.getCompanyName());
        existingCompany.setPassword(encodedPassword);
        existingCompany.setCompanyAddress(request.getCompanyAddress());
        existingCompany.setRole(ROLE.COMPANY_ADMIN);
        existingCompany.setCompanySize(request.getCompanySize());
        existingCompany.setDeactivated(false);
//        existingCompany.setSignupToken(verificationToken);
        existingCompany.setEnabled(true);

        companyRepository.save(existingCompany);

//        sendVerificationEmail(email, verificationToken);

        return BusinessRegistrationResponse.builder()
                .id(existingCompany.getId())
                .email(existingCompany.getCompanyEmail())
                .businessName(existingCompany.getCompanyName())
                .domainName("Not Applicable")
                .businessAddress(existingCompany.getCompanyAddress())
                .build();
    }

    private void sendVerificationEmail(String recipient, String verificationToken) throws IOException {
        String verificationLink = "http://localhost:9191/api/auth/verify?token=" + verificationToken;
        String subject = "Account Verification";
        String messageBody = "Please click on the link below to verify your account:\n" + verificationLink;
        EmailDetails emailDetails = new EmailDetails(recipient, subject, messageBody);
        emailService.sendEmail(emailDetails);
    }

    public String inviteStaff(StaffInvitation staffInvitation) throws IOException {

        log.info("Entering invite staff method");

        String staffEmail = staffInvitation.getUserEmail();
        Company company = getAuthenticatedCompany();
        boolean isExistingVendor = vendorRepository.existsByEmail(staffEmail);
        boolean isExistingUser = userRepository.existsByEmail(staffEmail);
        boolean isExistingCompany = companyRepository.existsByCompanyEmail(staffEmail);
        boolean isExistingAdmin = adminRepository.existsByEmail(staffEmail);

        if (isExistingUser) {
            throw new CustomException("User with email " + staffEmail + " already exists!");
        }
        if (isExistingVendor) {
            throw new CustomException("Vendor with email " + staffEmail + " already exists!");
        }
        if (isExistingCompany) {
            throw new CustomException("Company with email " + staffEmail + " already exists!");
        }
        if (isExistingAdmin) {
            throw new CustomException("Admin with email " + staffEmail + " already exists!");
        }

        String signupToken = generateToken();

        User staffUser = new User();
        staffUser.setEmail(staffEmail);
        staffUser.setRole(ROLE.COMPANY_STAFF);
        staffUser.setEnabled(true);
        staffUser.setActive(true);
        staffUser.setCompany(company);
        staffUser.setVerificationToken(signupToken);

        userRepository.save(staffUser);

        String invitationLink = "http://localhost:9191/api/auth/staff-signup?email=" + URLEncoder.encode(staffEmail, StandardCharsets.UTF_8) + "&token=" + signupToken;
        String subject = "Invitation to Sign Up";
        String messageBody = "Dear Staff,\n\nYou have been invited by " + company.getCompanyName() + " to sign up on the company's platform. Please click the link below to complete your registration:\n\n" + invitationLink + "\n\nNote from the admin: " + staffInvitation.getNote();
        EmailDetails emailDetails = new EmailDetails(staffEmail, subject, messageBody);

        // Send the invitation email
        emailService.sendEmail(emailDetails);

        return "Staff onboarded successfully. Email sent to staff to complete registration";
    }

    private Company getAuthenticatedCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String companyEmail = authentication.getName();
        Company company = companyRepository.findByCompanyEmail(companyEmail);
        if (company == null) {
            throw new CustomException("Company not found");
        }
        return company;
    }

    @Override
    public String forgotPassword(String email) throws IOException {
        Company company = companyRepository.findByCompanyEmail(email);

        if (company == null) {
            throw new CustomException("Company with " + email + " does not exist");
        }

        String resetToken = generateResetToken();
        company.setVerificationToken(resetToken);
        companyRepository.save(company);

        // Send password reset email
        sendPasswordResetEmail(email, resetToken);

        return "Password reset code has been sent to your email address!!!.";
    }


    public BusinessRegistrationResponse updateCompanyProfile(String companyName, String companyAddress,
                                                             String phoneNumber, CompanySize companySize, MultipartFile file) throws IOException {
        Company existingCompany = getAuthenticatedCompany();

        String imageUrl = existingCompany.getImageUrl();

        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", companyName,
                            "folder", "images",
                            "overwrite", true,
                            "resource_type", "auto"
                    ));
            imageUrl = uploadResult.get("secure_url").toString();
        }

        existingCompany.setCompanyName(companyName);
        existingCompany.setCompanyAddress(companyAddress);
        existingCompany.setPhoneNumber(phoneNumber);
        existingCompany.setCompanySize(companySize);
        existingCompany.setImageUrl(imageUrl);

        companyRepository.save(existingCompany);

        return BusinessRegistrationResponse.builder()
                .id(existingCompany.getId())
                .email(existingCompany.getCompanyEmail())
                .businessName(existingCompany.getCompanyName())
                .domainName("Not Applicable")
                .businessAddress(existingCompany.getCompanyAddress())
                .imageUrl(existingCompany.getImageUrl())
                .build();
    }


    public CompanyResponse viewCompanyProfile() {
        Company existingCompany = getAuthenticatedCompany();

        return CompanyResponse.builder()
                .id(existingCompany.getId())
                .companyEmail(existingCompany.getCompanyEmail())
                .companyName(existingCompany.getCompanyName())
                .companyAddress(existingCompany.getCompanyAddress())
                .imageUrl(existingCompany.getImageUrl())
                .companySize(existingCompany.getCompanySize())
                .build();
    }

    @Override
    public ReviewResponse addRatingAndReview(Review review, String vendorId, ReviewRequest reviewRequest) {

        Integer rating = reviewRequest.getRating();
        String comment = reviewRequest.getComment();

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found"));

        Company company = getAuthenticatedCompany();

        List<Review> existingCompanyReviews = reviewRepository.findByVendorAndCompany(vendor, company);
        if (!existingCompanyReviews.isEmpty()) {
            throw new CustomException("Company has already reviewed this vendor!!!");
        }
        review.setRating(rating);
        review.setComment(comment);
        review.setVendor(vendor);
        review.setCompany(company);
        reviewRepository.save(review);

        List<Review> reviews = vendor.getReviews();
        double sumRatings = reviews.stream().mapToDouble(Review::getRating).sum();
        Double averageRating = reviews.isEmpty() ? 0.0 : sumRatings / reviews.size();

        vendor.setAverageRating(averageRating);
        vendor.setTotalRatings((long) reviews.size());

        vendorRepository.save(vendor);

        return ReviewResponse.builder()
                .id(vendor.getId())
                .businessName(vendor.getBusinessName())
                .averageRating(vendor.getAverageRating())
                .build();
    }

    private String generateResetToken() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        return String.format("%06d", randomNumber);
    }

    private void sendPasswordResetEmail(String recipient, String resetToken) throws IOException {
        String subject = "Password Reset";
        String messageBody = "Your password reset code is: " + resetToken;
        EmailDetails emailDetails = new EmailDetails(recipient, subject, messageBody);
        emailService.sendEmail(emailDetails);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
