package com.example.foodapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.constant.ROLE;
import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.exception.UserAlreadyExistException;
import com.example.foodapp.payloads.request.*;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.*;
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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

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
    private final OrderRepository orderRepository;
    private final SupplementRepository supplementRepository;
    private final ItemMenuRepository itemMenuRepository;

    @Override
    public BusinessRegistrationResponse companySignup(CompanyRegistrationRequest request) {

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
            throw new CustomException("User already exist with the email: " + email);
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
        existingCompany.setActive(true);
        existingCompany.setDomainName(request.getDomainName());
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

    @Override
    public String addVendor (String vendorId, String note) throws UserAlreadyExistException, IOException {
        Company company = getAuthenticatedCompany();
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new CustomException("Vendor does not exist"));
        String vendorEmail = vendor.getEmail();
        company.getVendors().add(vendor);
        companyRepository.save(company);
        String subject = "Invitation";
        String messageBody = "Dear Vendor,\n\nYou have been invited to service on our platform. \n\nNote from the admin: " + note;
        EmailDetails emailDetails = new EmailDetails(vendorEmail, subject, messageBody);
        emailService.sendEmail(emailDetails);
        log.info("Vendor onboarded successfully. Email sent to " + vendor + " to complete registration --------------");
        return "Vendor with " + vendor.getId() + " added to the list of vendors. Email sent to vendor";
    }

    @Override
    public List<DetailsResponse> getAllVendorDetails(){
        List<DetailsResponse> detailsResponses = new ArrayList<>();
        List<Vendor> vendors = vendorRepository.findAll();

        /*CustomFileHandler customFileHandler = new CustomFileHandler();
        logger.addHandler(customFileHandler);

        try {*/
        for (Vendor vendor : vendors) {
            DetailsResponse detailsResponse = new DetailsResponse();
            detailsResponse.setId(vendor.getId());
            detailsResponse.setVendorEmail(vendor.getEmail());
            detailsResponse.setBusinessName(vendor.getBusinessName());
            detailsResponse.setAddress(vendor.getBusinessAddress());
            detailsResponse.setContactNumber(vendor.getPhone());
            detailsResponse.setLastAccessed(vendor.getUpdatedAt());
            detailsResponse.setTotalRatings(vendor.getTotalRatings());
            detailsResponse.setAverageRating(vendor.getAverageRating());
            detailsResponse.setActive(vendor.getActive());
            //detailsResponse.setItemCategories(vendor.getItemCategory());
            detailsResponses.add(detailsResponse);

            //logger.info("Added details for vendor " + vendor);
        }
        //logger.info("Vendors details fetched successfully!!! -----------------------------------------\n");
        //logger.removeHandler(customFileHandler);
        return detailsResponses;
        /*} finally {
            logger.removeHandler(customFileHandler);
        }*/
    }

    public List<OrderDetailsResponse> viewOrdersByCompanyStaff() {
        Company company = getAuthenticatedCompany();
        List<Order> allOrders = orderRepository.findAll();
        List<Order> ordersByCompanyStaff = allOrders.stream()
                .filter(order -> order.getUser().getCompany().equals(company))
                .toList();

        List<OrderDetailsResponse> orderDetailsResponses = new ArrayList<>();
        for (Order order : ordersByCompanyStaff) {
            OrderDetailsResponse orderDetails =new OrderDetailsResponse();
            orderDetails.setOrderId(order.getOrderId());
            orderDetails.setOrderDate(order.getCreatedAt());
            orderDetails.setAmount(order.getTotalAmount());
            orderDetails.setDeliveryStatus(order.getDeliveryStatus());
            orderDetailsResponses.add(orderDetails);
        }
        return orderDetailsResponses;
    }

    @Override
    public OrderViewResponse viewUserOrderDetailsByCompany(String orderId) {

        Company company = getAuthenticatedCompany();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found"));

        if (!order.getUser().getCompany().equals(company)) {
            throw new CustomException("This order does not belong to your company");
        }
        OrderViewResponse orderResponse = viewAllOrdersInternal(Collections.singletonList(order));
        return orderResponse;
    }

    public List<OrderResponse> viewStaffLastOrder (String staffId) {
        Company company = getAuthenticatedCompany();

        User staff = userRepository.findById(staffId).orElseThrow(()-> new CustomException("Staff not found!!!"));

        if(!company.getUserList().contains(staff)){
            throw new CustomException("Staff does not belong to company!!!");
        }
        List<Order> lastThreeOrders = orderRepository.findTop3ByUserAndOrderByCreatedAtDesc(staff);

        if(lastThreeOrders.isEmpty()) {
            throw new CustomException("Orders not found!!!");
        }

        List<OrderResponse> orderResponses = new ArrayList<>();

        for(Order order : lastThreeOrders) {

            if (!order.getUser().getCompany().getUserList().contains(staff)) {
                throw new CustomException("Order does not belong to the company");
            }
            OrderResponse orderResponse = OrderResponse.builder()
                    .orderId(order.getOrderId())
                    .items(new ArrayList<>())
                    .totalAmount(order.getTotalAmount())
                    .build();

            for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
                String itemId = entry.getKey();
                int quantity = entry.getValue();

                ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
                Vendor vendor = itemMenu.getItemCategory().getVendor();

                FoodDataResponse foodDataResponse = FoodDataResponse.builder()
                        .itemId(itemId)
                        .itemName(itemMenu.getItemName())
                        .quantity(quantity)
                        .price(itemMenu.getItemPrice())
                        .vendorName(vendor.getBusinessName())
                        .build();

                orderResponse.getItems().add(foodDataResponse);
            }

            for (Map.Entry<String, Integer> entry : order.getSupplements().entrySet()) {
                String supplementId = entry.getKey();
                int quantity = entry.getValue();

                Supplement supplement = supplementRepository.findBySupplementId(supplementId);
                Vendor vendor = supplement.getVendor();

                FoodDataResponse foodDataResponse = FoodDataResponse.builder()
                        .itemId(supplementId)
                        .itemName(supplement.getSupplementName())
                        .quantity(quantity)
                        .vendorName(vendor.getBusinessName())
                        .price(supplement.getSupplementPrice())
                        .build();

                orderResponse.getItems().add(foodDataResponse);
            }

            orderResponses.add(orderResponse);

        }
        return orderResponses;
    }

    @Override
    public String removeVendor (String vendorId, String note) throws UserAlreadyExistException, IOException {
        Company company = getAuthenticatedCompany();
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new CustomException("Vendor does not exist"));
        String vendorEmail = vendor.getEmail();
        company.getVendors().remove(vendor);
        companyRepository.save(company);

        String subject = "Deactivated";
        String messageBody = "Dear Vendor,\n\nYou have been deactivated from our list of vendors. \n\nNote from the admin: " + note;
        EmailDetails emailDetails = new EmailDetails(vendorEmail, subject, messageBody);

        emailService.sendEmail(emailDetails);

        log.info("Vendor removed successfully. Email sent to " + vendor + " --------------");

        return "Vendor with " + vendor.getId() + " removed from the list of vendors. Email sent to vendor";
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
                                                             String phoneNumber, CompanySize companySize, String domainName,
                                                             BigDecimal priceLimit, MultipartFile file) throws IOException {
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
        existingCompany.setDomainName(domainName);
        existingCompany.setPriceLimit(priceLimit);
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
    public CompanyDetailsResponse getCompanyDashboard() {
        Company company = getAuthenticatedCompany();
        Integer numberOfStaff = userRepository.countByCompanyAndRole(company, ROLE.COMPANY_STAFF);
        Integer numberOfVendors = company.getVendors().size();

        List<Order> ordersByCompany = orderRepository.findByUserCompany(company);
        BigDecimal totalAmountSpent = ordersByCompany.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CompanyDetailsResponse.builder()
                .numberOfStaff(numberOfStaff)
                .numberOfVendors(numberOfVendors)
                .totalAmountSpent(totalAmountSpent)
                .build();
    }

    public List<GraphReportDTO> generateCompanySpendingReport(LocalDate startDate, LocalDate endDate, TimeFrame timeFrame) {
        Company authenticatedCompany = getAuthenticatedCompany();
        List<GraphReportDTO> spendingReport = new ArrayList<>();

        if (timeFrame == TimeFrame.DAILY) {
            while (!startDate.isAfter(endDate)) {
                BigDecimal totalSpendingForDay = orderRepository.sumTotalAmountByUserCompanyAndCreatedAtBetween(
                        authenticatedCompany,
                        startDate.atStartOfDay(),
                        startDate.atTime(23, 59, 59)
                );

                spendingReport.add(new GraphReportDTO(
                        startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                        totalSpendingForDay != null ? totalSpendingForDay : BigDecimal.ZERO
                ));

                startDate = startDate.plusDays(1);
            }
        } else if (timeFrame == TimeFrame.WEEKLY) {
            startDate = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            while (!startDate.isAfter(endDate)) {
                LocalDate endDateOfWeek = startDate.plusDays(6);
                BigDecimal totalSpendingForWeek = orderRepository.sumTotalAmountByUserCompanyAndCreatedAtBetween(
                        authenticatedCompany,
                        startDate.atStartOfDay(),
                        endDateOfWeek.atTime(23, 59, 59)
                );

                spendingReport.add(new GraphReportDTO(
                        startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                        totalSpendingForWeek != null ? totalSpendingForWeek : BigDecimal.ZERO
                ));

                startDate = startDate.plusWeeks(1);
            }
        } else if (timeFrame == TimeFrame.MONTHLY) {
            startDate = startDate.with(TemporalAdjusters.firstDayOfMonth());
            while (!startDate.isAfter(endDate)) {
                LocalDate endDateOfMonth = startDate.with(TemporalAdjusters.lastDayOfMonth());
                BigDecimal totalSpendingForMonth = orderRepository.sumTotalAmountByUserCompanyAndCreatedAtBetween(
                        authenticatedCompany,
                        startDate.atStartOfDay(),
                        endDateOfMonth.atTime(23, 59, 59)
                );

                spendingReport.add(new GraphReportDTO(
                        startDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                        totalSpendingForMonth != null ? totalSpendingForMonth : BigDecimal.ZERO
                ));

                startDate = startDate.plusMonths(1);
            }
        } else {
            throw new IllegalArgumentException("Invalid time frame");
        }

        return spendingReport;
    }

    @Override
    public UserResponse viewCompanyStaff(String staffId) {

        Company company = getAuthenticatedCompany();
        User user = userRepository.findById(staffId).orElseThrow(()-> new CustomException("Staff not found"));

        if (!company.getUserList().contains(user)){
            throw new CustomException("Staff does not belong to company");
        }

        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setUserCompany(user.getCompany().getCompanyName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setTotalSpending(orderRepository.totalSpendingByUser(user));
        userResponse.setLastOrder(orderRepository.lastOrderByUser(user));
        userResponse.setProfilePictureUrl(user.getProfilePictureUrl());

        return userResponse;
    }

    @Override
    public List<UserResponse> getCompanyStaff() {
        Company company = getAuthenticatedCompany();
        List<UserResponse> userResponses = new ArrayList<>();
        List<User> users = company.getUserList();

        for(User user : users){
            UserResponse userResponse = new UserResponse();
            userResponse.setUserId(user.getId());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setEmail(user.getEmail());
            userResponse.setPhone(user.getPhone());
            userResponse.setCreatedAt(user.getCreatedAt());
            userResponse.setTotalSpending(orderRepository.totalSpendingByUser(user));
            userResponse.setLastOrder(orderRepository.lastOrderByUser(user));
            userResponse.setProfilePictureUrl(user.getProfilePictureUrl());

            userResponses.add(userResponse);
        }
        return userResponses;
    }

    @Override
    public List<DetailsResponse> getCompanyVendors() {
        Company company = getAuthenticatedCompany();
        List<DetailsResponse> detailsResponses = new ArrayList<>();
        List<Vendor> vendors = company.getVendors();

        /*CustomFileHandler customFileHandler = new CustomFileHandler();
        logger.addHandler(customFileHandler);

        try {*/
        for (Vendor vendor : vendors) {
            DetailsResponse detailsResponse = new DetailsResponse();
            detailsResponse.setId(vendor.getId());
            detailsResponse.setVendorEmail(vendor.getEmail());
            detailsResponse.setBusinessName(vendor.getBusinessName());
            detailsResponse.setAddress(vendor.getBusinessAddress());
            detailsResponse.setContactNumber(vendor.getPhone());
            detailsResponse.setLastAccessed(vendor.getUpdatedAt());
            detailsResponse.setTotalRatings(vendor.getTotalRatings());
            detailsResponse.setAverageRating(vendor.getAverageRating());
            detailsResponse.setActive(vendor.getActive());
            //detailsResponse.setItemCategories(vendor.getItemCategory());
            detailsResponses.add(detailsResponse);

            //logger.info("Added details for vendor " + vendor);
        }
        //logger.info("Vendors details fetched successfully!!! -----------------------------------------\n");
        //logger.removeHandler(customFileHandler);
        return detailsResponses;
        /*} finally {
            logger.removeHandler(customFileHandler);
        }*/
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

    private OrderViewResponse viewAllOrdersInternal(List<Order> orderList) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        int totalItems = 0;
        BigDecimal totalSum = BigDecimal.ZERO;

        for (Order order : orderList) {
            List<FoodDataResponse> foodDataResponses = new ArrayList<>();

            // Iterate through the cart items
            for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
                String itemId = entry.getKey();
                int quantity = entry.getValue();

                // Retrieve the ItemMenu object from your data source using itemId
                ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);

                totalItems += quantity;

                Vendor vendor = itemMenu.getItemCategory().getVendor();
                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(itemId)
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .imageUri(itemMenu.getImageUrl())
                        .quantity(quantity)
                        .vendorName(vendor.getBusinessName())
                        .build());
            }

            // Iterate through the cart supplements
            for (Map.Entry<String, Integer> entry : order.getSupplements().entrySet()) {
                String supplementId = entry.getKey();
                int quantity = entry.getValue();

                // Retrieve the Supplement object from your data source using supplementId
                Supplement supplement = supplementRepository.findById(supplementId)
                        .orElseThrow(() -> new CustomException("Supplement not found!!!"));

                totalItems += quantity;

                foodDataResponses.add(FoodDataResponse.builder()
                        .itemId(supplementId)
                        .itemName(supplement.getSupplementName())
                        .price(supplement.getSupplementPrice())
                        .quantity(quantity)
                        .build());
            }

            orderResponses.add(OrderResponse.builder()
                    .orderId(order.getOrderId())
                    .items(foodDataResponses)
                    .totalAmount(order.getTotalAmount())
                    .build());

            totalSum = totalSum.add(order.getTotalAmount());
        }

        OrderSummary orderSummary = OrderSummary.builder()
                .totalItems(totalItems)
                .totalSum(totalSum)
                .build();

        return new OrderViewResponse(orderResponses, orderSummary);
    }
}
