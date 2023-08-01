package com.example.foodapp.service.impl;

import com.example.foodapp.dto.request.EmailDetails;
import com.example.foodapp.dto.request.VendorRegistrationRequest;
import com.example.foodapp.dto.response.BusinessRegistrationResponse;
import com.example.foodapp.dto.response.FoodDataResponse;
import com.example.foodapp.dto.response.OrderResponse;
import com.example.foodapp.dto.response.OrderSummary;
import com.example.foodapp.entities.ItemMenu;
import com.example.foodapp.entities.Order;
import com.example.foodapp.entities.User;
import com.example.foodapp.entities.Vendor;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.exception.ResourceNotFoundException;
import com.example.foodapp.repository.CompanyRepository;
import com.example.foodapp.repository.OrderRepository;
import com.example.foodapp.repository.UserRepository;
import com.example.foodapp.repository.VendorRepository;
import com.example.foodapp.service.EmailService;
import com.example.foodapp.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final PasswordEncoder passwordEncoder;
    private final VendorRepository vendorRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final OrderRepository orderRepository;

    @Override
    public BusinessRegistrationResponse vendorSignup(VendorRegistrationRequest request) throws IOException {
        boolean isExistingVendor = vendorRepository.existsByEmail(request.getEmail());
        if (!isExistingVendor) {
            throw new CustomException("Invalid Vendor. Contact Admin!");
        }

        boolean isExistingUser = userRepository.existsByEmail(request.getEmail());
        if(isExistingUser) {
            throw new CustomException("User with " + request.getEmail() + " already exist!");
        }

        boolean isExistingCompany = companyRepository.existsByCompanyEmail(request.getEmail());
        if (isExistingCompany) {
            throw new CustomException("Company with email already exist!");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("Password does not match");
        }

        Vendor existingVendor = vendorRepository.findByEmail(request.getEmail());

        if (!existingVendor.getEnabled()) {
            throw new CustomException("Invalid Vendor. Contact Admin");
        }
        if(existingVendor.getPassword() != null) {
            throw new CustomException("User already exist with email");
        }

//        String verificationToken = generateToken();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        existingVendor.setFirstName(request.getFirstName());
        existingVendor.setLastName(request.getLastName());
        existingVendor.setPhone(request.getPhone());
        existingVendor.setPassword(encodedPassword);
        existingVendor.setBusinessName(request.getBusinessName());
        existingVendor.setBusinessAddress(request.getBusinessAddress());
        existingVendor.setDomainName(request.getDomainName());
        existingVendor.setDeactivated(false);
        existingVendor.setEnabled(true);

        vendorRepository.save(existingVendor);

//        sendVerificationEmail(request.getEmail(), verificationToken);

        return BusinessRegistrationResponse.builder()
                .id(existingVendor.getId())
                .email(existingVendor.getEmail())
                .businessName(existingVendor.getBusinessName())
                .domainName(existingVendor.getDomainName())
                .businessAddress(existingVendor.getBusinessAddress())
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
