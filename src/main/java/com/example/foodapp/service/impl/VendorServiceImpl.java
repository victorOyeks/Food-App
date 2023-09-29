package com.example.foodapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.OrderType;
import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.EmailDetails;
import com.example.foodapp.payloads.request.SalesReportDTO;
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
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final ItemMenuRepository itemMenuRepository;
    private final SupplementRepository supplementRepository;

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
        existingVendor.setActive(true);
        existingVendor.setEnabled(true);
        existingVendor.setStoreStatus(true);

        vendorRepository.save(existingVendor);

//        sendVerificationEmail(request.getEmail(), verificationToken);

        return BusinessRegistrationResponse.builder()
                .id(existingVendor.getId())
                .email(existingVendor.getEmail())
                .businessName(existingVendor.getBusinessName())
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
        existingVendor.setImageUrl(imageUrl);

        vendorRepository.save(existingVendor);

        return BusinessRegistrationResponse.builder()
                .id(existingVendor.getId())
                .email(existingVendor.getEmail())
                .businessName(existingVendor.getBusinessName())
                .businessAddress(existingVendor.getBusinessAddress())
                .mapUri(existingVendor.getMapUri())
                .imageUrl(existingVendor.getImageUrl())
                .build();
    }

    public String changePassword(ChangePasswordRequest request) {
        Vendor existingVendor = getAuthenticatedVendor();

        if (!passwordEncoder.matches(request.getOldPassword(), existingVendor.getPassword())) {
            throw new CustomException("Incorrect old password");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            System.out.println("New password: " + request.getNewPassword());
            System.out.println("Confirm password: " + request.getConfirmNewPassword());
            throw new CustomException("New password and confirm password do not match");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        existingVendor.setPassword(encodedPassword);

        vendorRepository.save(existingVendor);

        return "Password changed successfully!!!";
    }


    public List<OrderDetailsResponse> viewAllProcessedOrdersToVendor(TimeFrame timeFrame) {
        Vendor authenticatedVendor = getAuthenticatedVendor();
        List<Order> ordersByVendor = orderRepository.findOrdersByVendor(authenticatedVendor);
        LocalDateTime now = LocalDateTime.now();
        List<OrderDetailsResponse> orderDetailsResponses = new ArrayList<>();

        for (Order order : ordersByVendor) {
            if (order.getDeliveryStatus() != DeliveryStatus.PENDING) {
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
                    orderDetails.setSubmitStatus(order.getSubmitStatus());

                    orderDetailsResponses.add(orderDetails);
                }
            }
        }
        return orderDetailsResponses;
    }

    public List<OrderDetailsResponse> viewAllPendingOrdersToVendor(TimeFrame timeFrame) {
        Vendor authenticatedVendor = getAuthenticatedVendor();
        List<Order> ordersByVendor = orderRepository.findOrdersByVendor(authenticatedVendor);
        LocalDateTime now = LocalDateTime.now();
        List<OrderDetailsResponse> orderDetailsResponses = new ArrayList<>();

        for (Order order : ordersByVendor) {
            if (order.getDeliveryStatus() == DeliveryStatus.PENDING) {
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
                    orderDetails.setSubmitStatus(order.getSubmitStatus());

                    orderDetailsResponses.add(orderDetails);
                }
            }
        }
        return orderDetailsResponses;
    }

    /*
    public List<OrderDetailsResponse> viewAllLiveOrdersToVendor() {
        Vendor authenticatedVendor = getAuthenticatedVendor();
        List<Order> ordersByVendor = orderRepository.findOrdersByVendor(authenticatedVendor);
        List<OrderDetailsResponse> orderDetailsResponses = new ArrayList<>();

        for (Order order : ordersByVendor) {
            if (order.getDeliveryStatus() == DeliveryStatus.PENDING) {

                    OrderDetailsResponse orderDetails = new OrderDetailsResponse();
                    orderDetails.setOrderId(order.getOrderId());
                    orderDetails.setOrderDate(order.getCreatedAt());
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
     */

    public AdminOrderResponse viewOrderByUserOrCompany(String orderId, String userIdOrCompanyId) {

        Vendor vendor = getAuthenticatedVendor();
        User user = userRepository.findById(userIdOrCompanyId).orElse(null);
        Company company = companyRepository.findById(userIdOrCompanyId).orElse(null);

        Order order = orderRepository.findByOrderIdAndVendorId(orderId, vendor.getId());
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

    public void changeDeliveryStatus(String orderId, DeliveryStatus deliveryStatus) {
        Order order = orderRepository.findByOrderId(orderId);

        if (order == null) {
            throw new CustomException("Order not found with ID: " + orderId);
        }
        order.setDeliveryStatus(deliveryStatus);
        orderRepository.save(order);
    }

    public void changeStoreStatus(Boolean storeStatus) {
        String vendorId = getAuthenticatedVendor().getId();
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new CustomException("Vendor not found!!!"));

        vendor.setStoreStatus(storeStatus);
        vendorRepository.save(vendor);
    }



    public BusinessRegistrationResponse viewVendorProfile() {
        Vendor existingVendor = getAuthenticatedVendor();

        return BusinessRegistrationResponse.builder()
                .id(existingVendor.getId())
                .email(existingVendor.getEmail())
                .businessName(existingVendor.getBusinessName())
                .businessAddress(existingVendor.getBusinessAddress())
                .imageUrl(existingVendor.getImageUrl())
                .mapUri(existingVendor.getMapUri())
                .status(existingVendor.getStoreStatus())
                .coordinates(existingVendor.getCoordinates())
                .build();
    }

    public VendorDashboardSummaryResponse getVendorSummary(TimeFrame timeFrame) {
        Vendor authenticatedVendor = getAuthenticatedVendor();

        LocalDateTime startDate;
        LocalDateTime endDate = LocalDateTime.now();

        switch (timeFrame) {
            case TODAY:
                startDate = LocalDateTime.now();
                break;
            case LAST_7_DAYS:
                startDate = endDate.minusDays(6);
                break;
            case LAST_30_DAYS:
                startDate = endDate.minusDays(29);
                break;
            default:
                throw new IllegalArgumentException("Invalid time frame");
        }

        Long totalOrders = orderRepository.countOrdersByVendorAndCreatedAtBetween(
                authenticatedVendor, startDate, endDate);

        Long totalMenus = itemMenuRepository.countMenusByVendor(authenticatedVendor, startDate, endDate);

        BigDecimal totalSales = orderRepository.sumTotalAmountByVendorAndCreatedAtBetween(
                authenticatedVendor, startDate, endDate);

        return new VendorDashboardSummaryResponse(totalOrders, totalMenus, totalSales);
    }

    public List<SalesReportDTO> generateSalesReport(LocalDate startDate, LocalDate endDate, TimeFrame timeFrame) {
        Vendor authenticatedVendor = getAuthenticatedVendor();
        List<SalesReportDTO> salesReport = new ArrayList<>();

        if (timeFrame == TimeFrame.DAILY) {
            while (!startDate.isAfter(endDate)) {
                BigDecimal totalSalesForDay = calculateTotalSalesForDay(authenticatedVendor, startDate);
                salesReport.add(new SalesReportDTO(
                        startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                        totalSalesForDay
                ));
                startDate = startDate.plusDays(1);
            }
        } else if (timeFrame == TimeFrame.WEEKLY) {
            startDate = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            while (!startDate.isAfter(endDate)) {
                BigDecimal totalSalesForWeek = calculateTotalSalesForWeek(authenticatedVendor, startDate);
                salesReport.add(new SalesReportDTO(
                        startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                        totalSalesForWeek
                ));
                startDate = startDate.plusWeeks(1);
            }
        } else if (timeFrame == TimeFrame.MONTHLY) {
            startDate = startDate.with(TemporalAdjusters.firstDayOfMonth());
            while (!startDate.isAfter(endDate)) {
                BigDecimal totalSalesForMonth = calculateTotalSalesForMonth(authenticatedVendor, startDate);
                salesReport.add(new SalesReportDTO(
                        startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                        totalSalesForMonth
                ));
                startDate = startDate.plusMonths(1);
            }
        } else {
            throw new IllegalArgumentException("Invalid time frame");
        }

        return salesReport;
    }


    private BigDecimal calculateTotalSalesForDay(Vendor vendor, LocalDate date) {
        // Calculate the total sales for the specified day
        BigDecimal totalSales = orderRepository.sumTotalAmountByVendorAndCreatedAtBetween(
                vendor,
                date.atStartOfDay(),
                date.atTime(23, 59, 59)
        );

        return totalSales != null ? totalSales : BigDecimal.ZERO;
    }

    private BigDecimal calculateTotalSalesForWeek(Vendor vendor, LocalDate startDate) {
        // Calculate the total sales for the specified week
        LocalDate endDate = startDate.plusDays(6);
        BigDecimal totalSales = orderRepository.sumTotalAmountByVendorAndCreatedAtBetween(
                vendor,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        return totalSales != null ? totalSales : BigDecimal.ZERO;
    }

    private BigDecimal calculateTotalSalesForMonth(Vendor vendor, LocalDate startDate) {
        // Calculate the total sales for the specified month
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        BigDecimal totalSales = orderRepository.sumTotalAmountByVendorAndCreatedAtBetween(
                vendor,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
        );

        return totalSales != null ? totalSales : BigDecimal.ZERO;
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

        // Accumulate quantities for each item menu
        Map<String, Integer> accumulatedQuantities = new HashMap<>();

        // Iterate through the itemMenus map
        for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            // Accumulate quantities for each item menu
            accumulatedQuantities.put(itemId, accumulatedQuantities.getOrDefault(itemId, 0) + quantity);
        }

        // Iterate through the accumulated quantities map
        for (Map.Entry<String, Integer> entry : accumulatedQuantities.entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
            Vendor vendor = itemMenu.getItemCategory().getVendor();

            for (int i = 0; i < quantity; i++) {
                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(itemId)
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .vendorName(vendor.getBusinessName())
                        .build());
            }
        }

        // Iterate through the supplements map
        for (Map.Entry<String, Integer> entry : order.getSupplements().entrySet()) {
            String supplementId = entry.getKey();
            int quantity = entry.getValue();

            Supplement supplement = supplementRepository.findBySupplementId(supplementId);

            // If the supplement was found, create FoodDataResponse objects for it
            if (supplement != null) {
                for (int i = 0; i < quantity; i++) {
                    foodDataResponses.add(FoodDataResponse.builder()
                            .itemId(supplement.getSupplementId())
                            .itemName(supplement.getSupplementName())
                            .price(supplement.getSupplementPrice())
                            .vendorName(supplement.getVendor().getBusinessName())
                            .build());
                }
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