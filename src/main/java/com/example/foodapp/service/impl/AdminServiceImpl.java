package com.example.foodapp.service.impl;

import com.example.foodapp.constant.OrderType;
import com.example.foodapp.constant.ROLE;
import com.example.foodapp.constant.TimeFrame;
import com.example.foodapp.payloads.request.ChangePasswordRequest;
import com.example.foodapp.payloads.request.CompanyInvitation;
import com.example.foodapp.payloads.request.EmailDetails;
import com.example.foodapp.payloads.request.VendorInvitation;
import com.example.foodapp.payloads.response.*;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.exception.UserAlreadyExistException;
import com.example.foodapp.repository.*;
import com.example.foodapp.service.AdminService;
import com.example.foodapp.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final EmailService emailService;
    private final CompanyRepository companyRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final OrderRepository orderRepository;
    private final ItemMenuRepository itemMenuRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final SupplementRepository supplementRepository;

    private static final Logger logger = Logger.getLogger(AdminServiceImpl.class.getName());

    @Transactional
    @CreatedBy
    @LastModifiedBy
    @Override
    public String inviteVendor(VendorInvitation vendorInvitation) throws UserAlreadyExistException, IOException {

        /*CustomFileHandler customFileHandler = new CustomFileHandler();
        logger.addHandler(customFileHandler);

        try {
            logger.info("Inviting vendor: " + vendorInvitation);
            logger.removeHandler(customFileHandler);*/

            String vendorEmail = vendorInvitation.getVendorEmail();
            String note = vendorInvitation.getNote();

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

            String signupToken = generateSignupToken();

            Vendor vendor = new Vendor();
            vendor.setEmail(vendorEmail);
            vendor.setRole(ROLE.VENDOR);
            vendor.setEnabled(true);
            vendor.setActive(true);
            vendor.setSignupToken(signupToken);
            vendorRepository.save(vendor);

            String invitationLink = "http://localhost:9191/api/auth/vendor-signup?email=" + URLEncoder.encode(vendorEmail, StandardCharsets.UTF_8) + "&token=" + signupToken;
            String subject = "Invitation to Sign Up";
            String messageBody = "Dear Vendor,\n\nYou have been invited to sign up on our platform. Please click the link below to complete your registration:\n\n" + invitationLink + "\n\nNote from the admin: " + note;
            EmailDetails emailDetails = new EmailDetails(vendorEmail, subject, messageBody);

            // Send the invitation email
            emailService.sendEmail(emailDetails);

            logger.info("Vendor onboarded successfully. Email sent to " + vendor + " to complete registration --------------");
            //logger.removeHandler(customFileHandler);
            return "Vendor onboarded successfully. Email sent to vendor to complete registration";
        /*}finally {
            logger.removeHandler(customFileHandler);
        }*/
    }

    @Override
    public String inviteCompany(CompanyInvitation companyInvitation) throws IOException {
        /*CustomFileHandler customFileHandler = new CustomFileHandler();
        logger.addHandler(customFileHandler);*/

        logger.info("Inviting company: " + companyInvitation);

        String companyEmail = companyInvitation.getCompanyEmail();
        String note = companyInvitation.getNote();

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

        String signupToken = generateSignupToken();

        Company company = new Company();
        company.setCompanyEmail(companyEmail);
        company.setRole(ROLE.COMPANY_ADMIN);
        company.setEnabled(true);
        company.setActive(true);
        company.setSignupToken(signupToken);
        companyRepository.save(company);

        String invitationLink = "http://localhost:9191/api/auth/company-signup?email=" + URLEncoder.encode(companyEmail, StandardCharsets.UTF_8) + "&token=" + signupToken;
        String subject = "Invitation to Sign Up";
        String messageBody = "Dear Company, \n\nYou have been invited to sign up on our platform. Please click the link below to complete your registration:\n\n" + invitationLink + "\n\nNote from the admin: " + note;
        EmailDetails emailDetails = new EmailDetails(companyEmail, subject, messageBody);

        emailService.sendEmail(emailDetails);

        //logger.info("Company with " + companyEmail + " onboarded successfully. Email sent to company to complete registration");

        return  "Company with " + companyEmail + " onboarded successfully. Email sent to company to complete registration";
    }

    @Override
    public void deactivateUser(String userId) throws IOException {

        //logger.info("Deactivating user with ID: " + userId);

        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException("User not found with id: " + userId));
        if (user != null) {
            user.setActive(false);
            userRepository.save(user);

            String subject = "Account Deactivated!";
            String messageBody = "Dear user, \n\n You account has been deactivated! Contact support for more information. Thank you";
            EmailDetails emailDetails = new EmailDetails(user.getEmail(), subject, messageBody);

            // Send the invitation email
            emailService.sendEmail(emailDetails);
            //logger.info("User deactivated: " + userId);
        }
    }

    @Override
    public void reactivateUser(String userId) throws IOException {

        //logger.info("Reactivating user with ID: " + userId);

        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException("User not found with id: " +userId));
        if (user != null) {
            user.setActive(true);
            userRepository.save(user);

            String subject = "Account Reactivated!";
            String messageBody = "Dear user, \n\n You account has been reactivated! Regards.";
            EmailDetails emailDetails = new EmailDetails(user.getEmail(), subject, messageBody);

            // Send the invitation email
            emailService.sendEmail(emailDetails);

            //logger.info("User reactivated: " + userId);
        }
    }

    @Override
    public void deactivateVendor(String userId) throws CustomException, IOException {

        //logger.info("Deactivating vendor with ID: " + userId);

        Vendor vendor = vendorRepository.findById(userId)
                .orElseThrow(() -> new CustomException("Vendor not found with id: " +userId));

        vendor.setActive(false);
        vendorRepository.save(vendor);

        System.out.println("sending email to " + vendor);
        String subject = "Account Deactivated!";
        String messageBody = "Dear user, \n\nYour account has been deactivated! Contact support for more information. Thank you.";
        EmailDetails emailDetails = new EmailDetails(vendor.getEmail(), subject, messageBody);

        // Send the deactivation email
        emailService.sendEmail(emailDetails);

        //logger.info("Vendor deactivated: " + userId);
    }

    @Override
    public void reactivateVendor(String vendorId) throws IOException {

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(()-> new CustomException("Vendor not found with id: " +vendorId));

        //logger.info("Reactivating vendor with ID: " + vendor);

        if(vendor != null) {
            vendor.setActive(true);
            vendorRepository.save(vendor);

            String subject = "Account Reactivated!";
            String messageBody = "Dear vendor, \n\n You account has been reactivated! Regards.";
            EmailDetails emailDetails = new EmailDetails(vendor.getEmail(), subject, messageBody);

            // Send the invitation email
            emailService.sendEmail(emailDetails);
            //logger.info("Vendor reactivated: " + vendorId);
        }
    }


    @Override
    public List<DetailsResponse> getAllVendorDetails() throws IOException {
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

    @Override
    public BusinessRegistrationResponse getVendor(String vendorId) {

        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new CustomException("Vendor not found!!!"));

        return BusinessRegistrationResponse.builder()
                .id(vendor.getId())
                .email(vendor.getEmail())
                .businessName(vendor.getBusinessName())
                .domainName(vendor.getDomainName())
                .phoneNumber(vendor.getPhone())
                .businessAddress(vendor.getBusinessAddress())
                .mapUri(vendor.getMapUri())
                .imageUrl(vendor.getImageUrl())
                .coordinates(vendor.getCoordinates())
                .status(vendor.getActive())
                .build();
    }

    /*
    @Override
    public List<CompanyResponse> getAllCompanyDetails() {
        List<CompanyResponse> companyResponse = new ArrayList<>();
        List<Company> companies = companyRepository.findAll();

            for (Company company : companies) {
                CompanyResponse detailsResponse = new CompanyResponse();
                detailsResponse.setId(company.getId());
                detailsResponse.setCompanyName(company.getCompanyName());
                detailsResponse.setCompanyEmail(company.getCompanyEmail());
                detailsResponse.setDomainName(company.getDomainName());
                detailsResponse.setPhoneNumber(company.getPhoneNumber());
                detailsResponse.setCompanyAddress(company.getCompanyAddress());
                detailsResponse.setCompanySize(company.getCompanySize());
                detailsResponse.setImageUrl(company.getImageUrl());
                detailsResponse.setStatus(company.getActive());

                companyResponse.add(detailsResponse);

                //logger.info("Added Company details for " + companies);
            }

            //logger.info("Company details fetched successfully!!! -----------------------------------------\n");

        return companyResponse;
    }
     */

    public List<CompanyResponse> getAllCompanyDetails(boolean active) {
        List<CompanyResponse> companyResponse = new ArrayList<>();
        List<Company> companies = companyRepository.findByActive(active);

        for (Company company : companies) {
            CompanyResponse detailsResponse = new CompanyResponse();
            detailsResponse.setId(company.getId());
            detailsResponse.setCompanyName(company.getCompanyName());
            detailsResponse.setCompanyEmail(company.getCompanyEmail());
            detailsResponse.setDomainName(company.getDomainName());
            detailsResponse.setPhoneNumber(company.getPhoneNumber());
            detailsResponse.setCompanyAddress(company.getCompanyAddress());
            detailsResponse.setCompanySize(company.getCompanySize());
            detailsResponse.setImageUrl(company.getImageUrl());
            detailsResponse.setStatus(company.getActive());

            companyResponse.add(detailsResponse);
        }

        return companyResponse;
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

    public List<CategoryResponse> getAllItemCategory() {
        List<ItemCategory> foodCategories = itemCategoryRepository.findAll();

        return foodCategories.stream()
                .map(foodCategory -> CategoryResponse.builder()
                        .categoryId(foodCategory.getCategoryId())
                        .categoryName(foodCategory.getCategoryName())
                        .itemMenus(foodCategory.getItemMenus())
                        .build())
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> getAllCustomers() {
        List<CustomerResponse> customerResponses = new ArrayList<>();

        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Order> userOrders = orderRepository.findOrdersByUserId(user.getId());
            BigDecimal totalAmountSpent = calculateTotalAmountSpent(userOrders);
            LocalDateTime lastOrderTime = findLastOrderTime(userOrders);

            CustomerResponse customerResponse = CustomerResponse.builder()
                    .customerId(user.getId())
                    .customerName(user.getFirstName() + " " + user.getLastName())
                    .dateJoined(user.getCreatedAt())
                    .customerType("Individual")
                    .totalAmountSpent(totalAmountSpent)
                    .lastOrderTime(lastOrderTime)
                    .build();

            customerResponses.add(customerResponse);
        }

        List<Company> companies = companyRepository.findAll();
        for (Company company : companies) {
            List<User> companyUsers = company.getUserList();
            BigDecimal totalAmountSpent = calculateTotalAmountSpentForCompany(companyUsers);
            LocalDateTime lastOrderTime = findLastOrderTimeForCompany(companyUsers);

            CustomerResponse customerResponse = CustomerResponse.builder()
                    .customerId(company.getId())
                    .customerName(company.getCompanyName())
                    .dateJoined(company.getCreatedAt())
                    .customerType("Company")
                    .totalAmountSpent(totalAmountSpent)
                    .lastOrderTime(lastOrderTime)
                    .build();

            customerResponses.add(customerResponse);
        }

        return customerResponses;
    }

    public List<ItemMenuInfoResponse> getAllItemMenus() {
        List<ItemMenu> allItemMenus = itemMenuRepository.findAll();

        // Group itemMenus by name and count the orders for each itemMenu
        Map<String, Long> itemMenuOrdersCountMap = getOrderCountByItemMenuName();

        List<ItemMenuInfoResponse> itemMenuInfoResponses = allItemMenus.stream()
                .map(itemMenu -> {
                    Long orderCount = itemMenuOrdersCountMap.getOrDefault(itemMenu.getItemName(), 0L);

                    return ItemMenuInfoResponse.builder()
                            .itemName(itemMenu.getItemName())
                            .orderCount(orderCount)
                            .updatedDate(itemMenu.getUpdatedAt())
                            .itemCategory(itemMenu.getItemCategory().getCategoryName())
                            .build();
                })
                .collect(Collectors.toList());

        return itemMenuInfoResponses;
    }

    @Override
    public List<ItemMenusInCategoriesResponse> getAllItemMenusInAllCategories(String vendorId) {

        List<ItemMenu> allItemMenus = itemMenuRepository.findAllByVendorId(vendorId);

        // Group itemMenus by name and count the orders for each itemMenu
        Map<String, Long> itemMenuOrdersCountMap = getOrderCountByItemMenuName();

        List<ItemMenusInCategoriesResponse> itemMenuInfoResponses = allItemMenus.stream()
                .map(itemMenu -> {
                    Long orderCount = itemMenuOrdersCountMap.getOrDefault(itemMenu.getItemName(), 0L);

                    return ItemMenusInCategoriesResponse.builder()
                            .itemName(itemMenu.getItemName())
                            .totalSales(orderCount)
                            .ratingByOrder(itemMenu.getAverageRating())
                            .image(itemMenu.getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        return itemMenuInfoResponses;
    }

    @Override
    public CategoryResponse getItemMenusInCategory(String vendorId, String categoryId) {

            ItemCategory itemCategory = itemCategoryRepository.findByVendorIdAndCategoryId(vendorId, categoryId)
                    .orElseThrow(() -> new CustomException("Food category not found for the vendor"));

            return CategoryResponse.builder()
                    .categoryId(itemCategory.getCategoryId())
                    .categoryName(itemCategory.getCategoryName())
                    .itemMenus(itemCategory.getItemMenus())
                    .build();
    }


    //TODO: dont delete the method below;
    /*public List<OrderDetailsResponse> viewAllOrders() {
        List<Order> allOrders = orderRepository.findAll();
        List<OrderDetailsResponse> orderDetailsResponses = new ArrayList<>();

        for (Order order : allOrders) {
            OrderDetailsResponse orderDetails = new OrderDetailsResponse();
            orderDetails.setOrderId(order.getOrderId());
            orderDetails.setOrderDate(order.getCreatedAt());
            orderDetails.setCustomerName(getCustomerName(order));
            orderDetails.setOrderType(getOrderType(order));
            orderDetails.setAmount(order.getTotalAmount());
            orderDetails.setDeliveryStatus(order.getDeliveryStatus());
            orderDetailsResponses.add(orderDetails);
        }
        return orderDetailsResponses;
    }*/


    public List<OrderDetailsResponse> viewOrdersByTimeFrame(TimeFrame timeFrame) {
        List<Order> allOrders = orderRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        List<OrderDetailsResponse> orderDetailsResponses = new ArrayList<>();

        for (Order order : allOrders) {
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
            }

            if (isInTimeFrame) {
                OrderDetailsResponse orderDetails = new OrderDetailsResponse();
                orderDetails.setOrderId(order.getOrderId());
                orderDetails.setOrderDate(orderDate);
                orderDetails.setCustomerName(getCustomerName(order));
                orderDetails.setProfilePic(getCustomerProfilePic(order));
                orderDetails.setOrderType(getOrderType(order));
                orderDetails.setAmount(order.getTotalAmount());
                orderDetails.setDeliveryStatus(order.getDeliveryStatus());
                orderDetails.setPaymentStatus(order.getPaymentStatus());
                orderDetailsResponses.add(orderDetails);
            }
        }
        return orderDetailsResponses;
    }

    /*
    public List<AdminOrderResponse> viewAllOrdersByUserOrCompany(String userIdOrCompanyId) {
        List<Order> orderList;
        List<AdminOrderResponse> orderResponses = new ArrayList<>();

        // Check if the provided ID belongs to a User or a Company
        User user = userRepository.findById(userIdOrCompanyId).orElse(null);
        Company company = companyRepository.findById(userIdOrCompanyId).orElse(null);

        if (user != null) {
            orderList = orderRepository.findOrdersByUserId(userIdOrCompanyId);
            addOrdersToResponse(orderList, orderResponses, OrderType.INDIVIDUAL, user.getFirstName() + " " + user.getLastName(), user.getProfilePictureUrl(), user.getPhone(), user.getEmail(), user.getActive());

        } else if (company != null) {
            orderList = orderRepository.findOrdersByCompanyId(userIdOrCompanyId);
            addOrdersToResponse(orderList, orderResponses, OrderType.COMPANY, company.getCompanyName(), company.getImageUrl(), company.getPhoneNumber(), company.getCompanyEmail(), company.getActive());
        } else {
            throw new CustomException("User or Company not found with ID: " + userIdOrCompanyId);
        }

        return orderResponses;
    }
     */

    public AdminOrderResponse viewOrderByUserOrCompany(String orderId, String userIdOrCompanyId) {
        // Check if the provided ID belongs to a User or a Company
        User user = userRepository.findById(userIdOrCompanyId).orElse(null);
        Company company = companyRepository.findById(userIdOrCompanyId).orElse(null);

        if (user != null) {
            Order order = orderRepository.findOrderByOrderIdAndUserId(orderId, userIdOrCompanyId);
            if (order != null) {
                return addOrdersToResponse(order, OrderType.INDIVIDUAL, user.getFirstName() + " " + user.getLastName(), user.getProfilePictureUrl(), user.getPhone(), user.getEmail(), user.getActive());
            }
        } else if (company != null) {
            Order order = orderRepository.findOrderByOrderIdAndCompanyId(orderId, userIdOrCompanyId);
            if (order != null) {
                return addOrdersToResponse(order, OrderType.COMPANY, company.getCompanyName(), company.getImageUrl(), company.getPhoneNumber(), company.getCompanyEmail(), company.getActive());
            }
        }
        throw new CustomException("Order not found with ID: " + orderId + " for User or Company ID: " + userIdOrCompanyId);
    }

    public String changePassword(ChangePasswordRequest request) {
        Admin existingAdmin = getAuthenticatedAdmin();

        if (!passwordEncoder.matches(request.getOldPassword(), existingAdmin.getPassword())) {
            throw new CustomException("Incorrect old password");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            System.out.println("New password: " + request.getNewPassword());
            System.out.println("Confirm password: " + request.getConfirmNewPassword());
            throw new CustomException("New password and confirm password do not match");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        existingAdmin.setPassword(encodedPassword);

        adminRepository.save(existingAdmin);

        return "Password changed successfully!!!";
    }

    public List<ItemNamesResponse> getAllItemsInCategory(String vendorId) {
        List<ItemCategory> foodCategories = itemCategoryRepository.findByVendorId(vendorId);

        return foodCategories.stream()
                .flatMap(foodCategory -> foodCategory.getItemMenus().stream())
                .map(itemMenu -> ItemNamesResponse.builder()
                        .categoryId(itemMenu.getItemCategory().getCategoryId())
                        .categoryName(itemMenu.getItemCategory().getCategoryName())
                        .itemName(itemMenu.getItemName())
                        .build())
                .collect(Collectors.toList());
    }

    /* //TODO: DO NOT DELETE THIS METHOD
    public List<OrderHistoryResponse> viewOrderHistoryByUserOrCompany(String userIdOrCompanyId) {

        List<Order> orderList;
        List<OrderHistoryResponse> orderResponses = new ArrayList<>();

        // Check if the provided ID belongs to a User or a Company
        User user = userRepository.findById(userIdOrCompanyId).orElse(null);
        Company company = companyRepository.findById(userIdOrCompanyId).orElse(null);

        if (user != null) {
            orderList = orderRepository.findOrdersByUserId(userIdOrCompanyId);
            addOrdersHistoryToResponse(orderList, orderResponses, OrderType.INDIVIDUAL, user.getFirstName() + " " + user.getLastName(), user.getProfilePictureUrl(), user.getPhone(), user.getEmail());
        } else if (company != null) {
            orderList = orderRepository.findOrdersByCompanyId(userIdOrCompanyId);
            addOrdersHistoryToResponse(orderList, orderResponses, OrderType.COMPANY, company.getCompanyName(), company.getImageUrl(), company.getPhoneNumber(), company.getCompanyEmail());
        } else {
            throw new CustomException("User or Company not found with ID: " + userIdOrCompanyId);
        }

        return orderResponses;
    }

    private void addOrdersHistoryToResponse(List<Order> orderList, List<OrderHistoryResponse> orderResponses, OrderType orderType, String name, String profilePic, String contactNumber, String email) {
        for (Order order : orderList) {
            OrderHistoryResponse orderResponse = new OrderHistoryResponse();
            orderResponse.setOrderId(order.getOrderId());
            orderResponse.setOrderDate(order.getCreatedAt());
            orderResponse.setCustomerName(name);
            orderResponse.setProfilePic(profilePic);
            orderResponse.setOrderType(orderType);
            orderResponse.setAmount(order.getTotalAmount());
            orderResponse.setDeliveryStatus(order.getDeliveryStatus());
            orderResponse.setContactNumber(contactNumber);
            orderResponse.setEmail(email);
            orderResponses.add(orderResponse);
        }
    }
    */


    /* private AdminOrderResponse addOrdersToResponse(Order order, OrderType orderType, String customerName, String profilePic, String phone, String email, Boolean active) {
        Map<String, FoodDataResponse> foodDataResponseMap = new HashMap<>();

        // Iterate through the cartItems map
        for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            // Retrieve the ItemMenu object from your data source using itemId
            ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
            Vendor vendor = itemMenu.getItemCategory().getVendor();

            // Check if a FoodDataResponse already exists for the item
            if (foodDataResponseMap.containsKey(itemId)) {
                // If yes, update the quantity
                FoodDataResponse existingResponse = foodDataResponseMap.get(itemId);
                existingResponse.setQuantity(existingResponse.getQuantity() + quantity);
            } else {
                // If no, create a new FoodDataResponse
                FoodDataResponse newResponse = FoodDataResponse.builder()
                        .itemId(itemId)
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .quantity(quantity)
                        .imageUri(itemMenu.getImageUrl())
                        .vendorName(vendor.getBusinessName())
                        .build();
                foodDataResponseMap.put(itemId, newResponse);
            }
        }

        List<FoodDataResponse> foodDataResponses = new ArrayList<>(foodDataResponseMap.values());

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
                .customerStatus(active)
                .build();
    }

     */

    private AdminOrderResponse addOrdersToResponse(Order order, OrderType orderType, String customerName, String profilePic, String phone, String email, Boolean active) {
        Map<String, FoodDataResponse> foodDataResponseMap = new HashMap<>();

        // Iterate through the itemMenus map (items)
        for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
            String itemId = entry.getKey();
            int quantity = entry.getValue();

            // Retrieve the ItemMenu object from your data source using itemId
            ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
            Vendor vendor = itemMenu.getItemCategory().getVendor();

            // Check if a FoodDataResponse already exists for the item
            if (foodDataResponseMap.containsKey(itemId)) {
                // If yes, update the quantity
                FoodDataResponse existingResponse = foodDataResponseMap.get(itemId);
                existingResponse.setQuantity(existingResponse.getQuantity() + quantity);
            } else {
                // If no, create a new FoodDataResponse
                FoodDataResponse newResponse = FoodDataResponse.builder()
                        .itemId(itemId)
                        .itemName(itemMenu.getItemName())
                        .price(itemMenu.getItemPrice())
                        .quantity(quantity)
                        .imageUri(itemMenu.getImageUrl())
                        .vendorName(vendor.getBusinessName())
                        .build();
                foodDataResponseMap.put(itemId, newResponse);
            }
        }

        // Iterate through the supplements map
        for (Map.Entry<String, Integer> entry : order.getSupplements().entrySet()) {
            String supplementId = entry.getKey();
            int quantity = entry.getValue();

            // Retrieve the Supplement object from your data source using supplementId
            Supplement supplement = supplementRepository.findBySupplementId(supplementId);

            // Check if a FoodDataResponse already exists for the supplement
            if (foodDataResponseMap.containsKey(supplementId)) {
                // If yes, update the quantity
                FoodDataResponse existingResponse = foodDataResponseMap.get(supplementId);
                existingResponse.setQuantity(existingResponse.getQuantity() + quantity);
            } else {
                // If no, create a new FoodDataResponse
                FoodDataResponse newResponse = FoodDataResponse.builder()
                        .itemId(supplementId)
                        .itemName(supplement.getSupplementName())
                        .price(supplement.getSupplementPrice())
                        .quantity(quantity)
                        .vendorName(supplement.getVendor().getBusinessName())
                        .build();
                foodDataResponseMap.put(supplementId, newResponse);
            }
        }

        List<FoodDataResponse> foodDataResponses = new ArrayList<>(foodDataResponseMap.values());

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
                .customerStatus(active)
                .build();
    }

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

    private OrderType getOrderType(Order order) {
        if (order.getUser() != null) {
            return OrderType.INDIVIDUAL;
        } else if (order.getCompany() != null) {
            return OrderType.COMPANY;
        } else {
            return OrderType.UNKNOWN;
        }
    }

    /* private Map<String, Long> getOrderCountByItemMenuName() {
        List<Order> allOrders = orderRepository.findAll();
        Map<String, Long> itemMenuOrdersCountMap = new HashMap<>();

        for (Order order : allOrders) {
            for (ItemMenu itemMenu : order.getItemMenus()) {
                String itemName = itemMenu.getItemName();
                itemMenuOrdersCountMap.put(itemName, itemMenuOrdersCountMap.getOrDefault(itemName, 0L) + 1);
            }
        }
        return itemMenuOrdersCountMap;
    }

     */

    private Map<String, Long> getOrderCountByItemMenuName() {
        List<Order> allOrders = orderRepository.findAll();
        Map<String, Long> itemMenuOrdersCountMap = new HashMap<>();

        for (Order order : allOrders) {
            // Iterate through the cartItems map
            for (Map.Entry<String, Integer> entry : order.getItemMenus().entrySet()) {
                String itemId = entry.getKey();
                int quantity = entry.getValue();

                // Retrieve the ItemMenu object from your data source using itemId
                ItemMenu itemMenu = itemMenuRepository.findByItemId(itemId);
                String itemName = itemMenu.getItemName();

                // Increment the count by the quantity
                itemMenuOrdersCountMap.put(itemName, itemMenuOrdersCountMap.getOrDefault(itemName, 0L) + quantity);
            }
        }
        return itemMenuOrdersCountMap;
    }

    private BigDecimal calculateTotalAmountSpent(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDateTime findLastOrderTime(List<Order> orders) {
        return orders.stream()
                .map(Order::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    private BigDecimal calculateTotalAmountSpentForCompany(List<User> users) {
        BigDecimal totalAmountSpent = BigDecimal.ZERO;
        for (User user : users) {
            List<Order> userOrders = orderRepository.findOrdersByUserId(user.getId());
            totalAmountSpent = totalAmountSpent.add(calculateTotalAmountSpent(userOrders));
        }
        return totalAmountSpent;
    }

    private LocalDateTime findLastOrderTimeForCompany(List<User> users) {
        LocalDateTime lastOrderTime = null;
        for (User user : users) {
            List<Order> userOrders = orderRepository.findOrdersByUserId(user.getId());
            LocalDateTime userLastOrderTime = findLastOrderTime(userOrders);
            if (userLastOrderTime != null && (lastOrderTime == null || userLastOrderTime.isAfter(lastOrderTime))) {
                lastOrderTime = userLastOrderTime;
            }
        }
        return lastOrderTime;
    }

    private String generateSignupToken() {
        UUID signupToken = UUID.randomUUID();
        return signupToken.toString();
    }

    private Admin getAuthenticatedAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Admin admin = adminRepository.findByEmail(email);
        if (admin == null) {
            throw new CustomException("Admin not found");
        }
        return admin;
    }
}