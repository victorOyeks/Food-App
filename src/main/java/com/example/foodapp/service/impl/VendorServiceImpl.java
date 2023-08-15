package com.example.foodapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.foodapp.dto.request.EmailDetails;
import com.example.foodapp.dto.request.VendorRegistrationRequest;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.*;
import com.example.foodapp.service.EmailService;
import com.example.foodapp.service.VendorService;
import com.example.foodapp.utils.geoLocation.GeoLocation;
import com.example.foodapp.utils.geoLocation.GeoResponse;
import com.example.foodapp.utils.geoLocation.LocationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.example.foodapp.utils.geoLocation.LocationUtils.*;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final PasswordEncoder passwordEncoder;
    private final VendorRepository vendorRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final OrderRepository orderRepository;
    private final Cloudinary cloudinary;
    private final ReviewRepository reviewRepository;

    @Override
    public BusinessRegistrationResponse vendorSignup(VendorRegistrationRequest request) {

        GeoResponse geoDetails = getGeoDetails(request);
        String actualLocation = extractActualLocation(geoDetails);
        GeoLocation coordinates = extractGeoLocation(geoDetails);

        String email = request.getEmail();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        boolean isExistingVendor = vendorRepository.existsByEmail(email);
        if (!isExistingVendor) {
            throw new CustomException("Invalid Vendor. Contact Admin!");
        }

        boolean isExistingUser = userRepository.existsByEmail(email);
        if(isExistingUser) {
            throw new CustomException("User with " + email + " already exist!");
        }

        boolean isExistingCompany = companyRepository.existsByCompanyEmail(email);
        if (isExistingCompany) {
            throw new CustomException("Company with email already exist!");
        }

        if (!password.equals(confirmPassword)) {
            throw new CustomException("Password does not match");
        }

        Vendor existingVendor = vendorRepository.findByEmail(email);

        if (!existingVendor.getEnabled()) {
            throw new CustomException("Invalid Vendor. Contact Admin");
        }
        if(existingVendor.getPassword() != null) {
            throw new CustomException("User already exist with email");
        }

//        String verificationToken = generateToken();
        String encodedPassword = passwordEncoder.encode(password);

        String mapUri = LocationUtils.getMapUri(coordinates);

        existingVendor.setFirstName(request.getFirstName());
        existingVendor.setLastName(request.getLastName());
        existingVendor.setPhone(request.getPhone());
        existingVendor.setPassword(encodedPassword);
        existingVendor.setBusinessName(request.getBusinessName());
        existingVendor.setBusinessAddress(actualLocation);
        existingVendor.setCoordinates(coordinates);
        existingVendor.setMapUri(mapUri);
        existingVendor.setDomainName(request.getDomainName());
        existingVendor.setActive(true);
        existingVendor.setEnabled(true);

        vendorRepository.save(existingVendor);

//        sendVerificationEmail(request.getEmail(), verificationToken);

        return BusinessRegistrationResponse.builder()
                .id(existingVendor.getId())
                .email(existingVendor.getEmail())
                .businessName(existingVendor.getBusinessName())
                .domainName(existingVendor.getDomainName())
                .businessAddress(existingVendor.getBusinessAddress())
                .mapUri(existingVendor.getMapUri())
                .build();
    }

    public BusinessRegistrationResponse updateVendorProfile(String firstName, String lastName,
                                                            String phone, String businessName, String domainName,
                                                            String businessAddress, MultipartFile file) throws IOException {

        GeoResponse geoDetails = getGeoDetails(businessAddress);
        String actualLocation = extractActualLocation(geoDetails);
        GeoLocation coordinates = extractGeoLocation(geoDetails);

        Vendor existingVendor = getAuthenticatedVendor();

        String imageUrl = existingVendor.getImageUrl(); // Save the current image URL before updating

        // Check if the profile photo is being updated
        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", businessName,
                            "folder", "images",
                            "overwrite", true,
                            "resource_type", "auto"
                    ));
            imageUrl = uploadResult.get("secure_url").toString();
        }

        String mapUri = LocationUtils.getMapUri(coordinates);

        existingVendor.setFirstName(firstName);
        existingVendor.setLastName(lastName);
        existingVendor.setPhone(phone);
        existingVendor.setBusinessName(businessName);
        existingVendor.setBusinessAddress(actualLocation);
        existingVendor.setCoordinates(coordinates);
        existingVendor.setMapUri(mapUri);
        existingVendor.setDomainName(domainName);
        existingVendor.setImageUrl(imageUrl);

        vendorRepository.save(existingVendor);

        return BusinessRegistrationResponse.builder()
                .id(existingVendor.getId())
                .email(existingVendor.getEmail())
                .businessName(existingVendor.getBusinessName())
                .domainName(existingVendor.getDomainName())
                .businessAddress(existingVendor.getBusinessAddress())
                .imageUrl(existingVendor.getImageUrl())
                .build();
    }

    public List<OrderResponse> viewAllOrdersToVendor() {
        Vendor vendor = getAuthenticatedVendor();
        List<Order> orders = orderRepository.findOrdersByVendor(vendor);

        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Order order : orders) {
            List<FoodDataResponse> foodDataResponses = new ArrayList<>();

            for (ItemMenu itemMenu : order.getItemMenu()) {
                Vendor orderVendor = itemMenu.getItemCategory().getVendor();
                if (orderVendor.equals(vendor)) {
                    foodDataResponses.add(FoodDataResponse.builder()
                            .itemId(itemMenu.getItemId())
                            .itemName(itemMenu.getItemName())
                            .price(itemMenu.getItemPrice())
                            .vendorName(vendor.getBusinessName())
                            .build());
                }
            }

            if (!foodDataResponses.isEmpty()) {
                orderResponses.add(OrderResponse.builder()
                        .orderId(order.getOrderId())
                        .items(foodDataResponses)
                        .totalAmount(order.getTotalAmount())
                        .build());
            }
        }
        return orderResponses;
    }

    public BusinessRegistrationResponse viewVendorProfile() {
        Vendor existingVendor = getAuthenticatedVendor();

        return BusinessRegistrationResponse.builder()
                .id(existingVendor.getId())
                .email(existingVendor.getEmail())
                .businessName(existingVendor.getBusinessName())
                .domainName(existingVendor.getDomainName())
                .businessAddress(existingVendor.getBusinessAddress())
                .imageUrl(existingVendor.getImageUrl())
                .build();
    }

    private void sendVerificationEmail(String recipient, String verificationToken) throws IOException {
        String verificationLink = "http://localhost:9191/api/auth/verify?token=" + verificationToken;
        String subject = "Account Verification";
        String messageBody = "Please click on the link below to verify your account:\n" + verificationLink;
        EmailDetails emailDetails = new EmailDetails(recipient, subject, messageBody);
        emailService.sendEmail(emailDetails);
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

    private String generateResetToken() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        return String.format("%06d", randomNumber); // Format the random number as a 6-digit string
    }

    private Vendor getAuthenticatedVendor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String vendorEmail = authentication.getName();
        Vendor vendor = vendorRepository.findByEmail(vendorEmail);
        if (vendor == null) {
            throw new CustomException("Vendor not found");
        }
        return vendor;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new CustomException("User not found");
        }
        return user;
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


    public OrderSummary calculateOrderSummary(List<OrderResponse> orders) {
        int totalItems = 0;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (OrderResponse orderResponse : orders) {
            totalItems += orderResponse.getItems().size();
            totalSum = totalSum.add(orderResponse.getTotalAmount());
        }
        return new OrderSummary(totalItems, totalSum);
    }
}
