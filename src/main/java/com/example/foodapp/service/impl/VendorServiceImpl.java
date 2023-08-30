package com.example.foodapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.foodapp.constant.OrderType;
import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.payloads.request.EmailDetails;
import com.example.foodapp.payloads.request.VendorRegistrationRequest;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.*;
import com.example.foodapp.service.EmailService;
import com.example.foodapp.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

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
    private final VendorReviewRepository vendorReviewRepository;
    private final ItemMenuRepository itemMenuRepository;

    @Override
    public BusinessRegistrationResponse vendorSignup(VendorRegistrationRequest request) {
/*
        GeoResponse geoDetails = getGeoDetails(request);
        String actualLocation = extractActualLocation(geoDetails);
        GeoLocation coordinates = extractGeoLocation(geoDetails);
*/

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

        //String mapUri = LocationUtils.getMapUri(coordinates);

        existingVendor.setFirstName(request.getFirstName());
        existingVendor.setLastName(request.getLastName());
        existingVendor.setPhone(request.getPhone());
        existingVendor.setPassword(encodedPassword);
        existingVendor.setBusinessName(request.getBusinessName());
        existingVendor.setBusinessAddress(request.getBusinessAddress());
//        existingVendor.setCoordinates(coordinates);
//        existingVendor.setMapUri(mapUri);
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
        /*
        GeoResponse geoDetails = getGeoDetails(businessAddress);
        String actualLocation = extractActualLocation(geoDetails);
        GeoLocation coordinates = extractGeoLocation(geoDetails);
        */

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

        //String mapUri = LocationUtils.getMapUri(coordinates);

        existingVendor.setFirstName(firstName);
        existingVendor.setLastName(lastName);
        existingVendor.setPhone(phone);
        existingVendor.setBusinessName(businessName);
        existingVendor.setBusinessAddress(businessAddress);
//        existingVendor.setCoordinates(coordinates);
//        existingVendor.setMapUri(mapUri);
        existingVendor.setDomainName(domainName);
        existingVendor.setImageUrl(imageUrl);

        vendorRepository.save(existingVendor);

        return BusinessRegistrationResponse.builder()
                .id(existingVendor.getId())
                .email(existingVendor.getEmail())
                .businessName(existingVendor.getBusinessName())
                .domainName(existingVendor.getDomainName())
                .businessAddress(existingVendor.getBusinessAddress())
                .mapUri(existingVendor.getMapUri())
                .imageUrl(existingVendor.getImageUrl())
                .build();
    }

//    public List<OrderResponse> viewAllOrdersToVendor() {
//        Vendor vendor = getAuthenticatedVendor();
//        List<Order> orders = orderRepository.findOrdersByVendor(vendor);
//
//        List<OrderResponse> orderResponses = new ArrayList<>();
//
//        for (Order order : orders) {
//            List<FoodDataResponse> foodDataResponses = new ArrayList<>();
//
//            for (ItemMenu itemMenu : order.getItemMenu()) {
//                Vendor orderVendor = itemMenu.getItemCategory().getVendor();
//                if (orderVendor.equals(vendor)) {
//                    foodDataResponses.add(FoodDataResponse.builder()
//                            .itemId(itemMenu.getItemId())
//                            .itemName(itemMenu.getItemName())
//                            .price(itemMenu.getItemPrice())
//                            .vendorName(vendor.getBusinessName())
//                            .build());
//                }
//            }
//
//            if (!foodDataResponses.isEmpty()) {
//                orderResponses.add(OrderResponse.builder()
//                        .orderId(order.getOrderId())
//                        .items(foodDataResponses)
//                        .totalAmount(order.getTotalAmount())
//                        .build());
//            }
//        }
//        return orderResponses;
//    }


    public List<OrderDetailsResponse> viewAllOrdersToVendor(TimeFrame timeFrame) {
        Vendor authenticatedVendor = getAuthenticatedVendor();
        List<Order> ordersByVendor = orderRepository.findOrdersByVendor(authenticatedVendor);
        LocalDateTime now = LocalDateTime.now();
        List<OrderDetailsResponse> orderDetailsResponses = new ArrayList<>();

        for (Order order : ordersByVendor) {
            LocalDateTime orderDate = order.getCreatedAt();
            boolean isInTimeFrame = false;

            switch (timeFrame) {
                case TODAY:
                    isInTimeFrame = orderDate.toLocalDate().equals(LocalDate.now());
                    break;
                case THIS_WEEK:
                    LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
                    isInTimeFrame = orderDate.isAfter(startOfWeek) && orderDate.isBefore(endOfWeek);
                    break;
                case THIS_MONTH:
                    LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
                    LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
                    isInTimeFrame = orderDate.isAfter(startOfMonth) && orderDate.isBefore(endOfMonth);
                    break;
                default:
                    timeFrame = null;
            }

            if (isInTimeFrame) {
                OrderDetailsResponse orderDetails = new OrderDetailsResponse();
                orderDetails.setOrderId(order.getOrderId());
                orderDetails.setOrderDate(orderDate);
                orderDetails.setCustomerName(getCustomerName(order));
                orderDetails.setProfilePic(getCustomerProfilePic(order));
                orderDetails.setAmount(order.getTotalAmount());
                orderDetails.setDeliveryStatus(order.getDeliveryStatus());
                orderDetails.setPaymentStatus(order.getPaymentStatus());

                orderDetailsResponses.add(orderDetails);
            }
        }
        return orderDetailsResponses;
    }

    /*
    public AdminOrderResponse viewOrderByUserOrCompany(String orderId, String userIdOrCompanyId) {
        // Check if the provided ID belongs to a Vendor
        Vendor vendor = getAuthenticatedVendor();
        User user = userRepository.findById(userIdOrCompanyId).orElse(null);
        Company company = companyRepository.findById(userIdOrCompanyId).orElse(null);

        Vendor existingVendor = vendorRepository.findById(vendor.getId()).orElse(null);

        if (existingVendor != null) {
            if (user != null) {
                Order order = orderRepository.findOrderByOrderIdAndVendorId(orderId, vendor.getId());
                return addOrdersToResponse(order, OrderType.INDIVIDUAL, order.getUser().getFirstName()+" "
                        +order.getUser().getFirstName(), order.getUser().getProfilePictureUrl(), order.getUser().getPhone(),
                        order.getUser().getEmail());
            } else if (company != null) {
                Order order = orderRepository.findOrderByOrderIdAndVendorId(orderId, vendor.getId());
                return addOrdersToResponse(order, OrderType.INDIVIDUAL, order.getCompany().getCompanyName(),
                        order.getCompany().getImageUrl(), order.getCompany().getPhoneNumber(),
                        order.getCompany().getCompanyEmail());
            } else {
                    throw new CustomException("Order not found with ID: " + orderId + " for Vendor ID: " + vendor.getId());
                }
        } else {
            throw new CustomException("Vendor not found with ID: " + vendor.getId());
        }
    }
     */

    public AdminOrderResponse viewOrderByUserOrCompany(String orderId, String userIdOrCompanyId) {

        Vendor vendor = getAuthenticatedVendor();
        User user = userRepository.findById(userIdOrCompanyId).orElse(null);
        Company company = companyRepository.findById(userIdOrCompanyId).orElse(null);

        Order order = orderRepository.findAnOrderByVendor(vendor, orderId);
        if (order != null) {
            // Check if the order belongs to the specified user or company
            if ((user != null && order.getUser().getId().equals(userIdOrCompanyId)) ||
                    (company != null && order.getCompany() != null && order.getCompany().getId().equals(userIdOrCompanyId))) {
                if (user != null) {
                    return addOrdersToResponse(order, OrderType.INDIVIDUAL, order.getUser().getFirstName() + " " +
                                    order.getUser().getLastName(), order.getUser().getProfilePictureUrl(),
                            order.getUser().getPhone(), order.getUser().getEmail());
                } else {
                    return addOrdersToResponse(order, OrderType.INDIVIDUAL, order.getCompany().getCompanyName(),
                            order.getCompany().getImageUrl(), order.getCompany().getPhoneNumber(),
                            order.getCompany().getCompanyEmail());
                }
            } else {
                throw new CustomException("Order with ID " + orderId + " does not belong to User/Company with ID " + userIdOrCompanyId);
            }
        } else {
            throw new CustomException("Order not found with ID: " + orderId + " for Vendor ID: " + vendor.getId());
        }
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
                .mapUri(existingVendor.getMapUri())
                .coordinates(existingVendor.getCoordinates())
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

    /*
    public OrderSummary calculateOrderSummary(List<OrderResponse> orders) {
        int totalItems = 0;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (OrderResponse orderResponse : orders) {
            totalItems += orderResponse.getItems().size();
            totalSum = totalSum.add(orderResponse.getTotalAmount());
        }
        return new OrderSummary(totalItems, totalSum);
    }

     */

    private String getCustomerName(Order order) {
        if (order.getUser() != null) {
            return order.getUser().getFirstName() + " " + order.getUser().getLastName();
        } else if (order.getCompany() != null) {
            return order.getCompany().getCompanyName();
        } else {
            return "Unknown Customer";
        }
    }

    private String getCustomerProfilePic(Order order) {
        if (order.getUser() != null) {
            return order.getUser().getProfilePictureUrl();
        } else if (order.getCompany() != null) {
            return order.getCompany().getImageUrl();
        } else {
            return "Unknown Customer";
        }
    }

    private AdminOrderResponse addOrdersToResponse(Order order, OrderType orderType, String customerName, String profilePic, String phone, String email) {
        List<FoodDataResponse> foodDataResponses = new ArrayList<>();

        for(Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
            String itemId = entry.getKey();
            Integer quantity = entry.getValue();

            ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
            Vendor vendor = itemMenu.getItemCategory().getVendor();

            for(int i = 0; i < quantity; i++) {

                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(itemMenu.getItemId())
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .vendorName(vendor.getBusinessName())
                        .build());
            }
        }
        return AdminOrderResponse.builder()
                .orderId(order.getOrderId())
                .items(foodDataResponses)
                .orderType(orderType)
                .customerName(customerName)
                .profilePic(profilePic)
                .phone(phone)
                .email(email)
                .totalAmount(order.getTotalAmount())
                .deliveryStatus(order.getDeliveryStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}