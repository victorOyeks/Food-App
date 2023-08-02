package com.example.foodapp.service.impl;

import com.example.foodapp.constant.ROLE;
import com.example.foodapp.dto.request.CompanyInvitation;
import com.example.foodapp.dto.request.EmailDetails;
import com.example.foodapp.dto.request.VendorInvitation;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.exception.UserAlreadyExistException;
import com.example.foodapp.repository.*;
import com.example.foodapp.service.AdminService;
import com.example.foodapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    @Override
    public String inviteVendor(VendorInvitation vendorInvitation) throws UserAlreadyExistException, IOException {
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
        company.setDeactivated(false);
        company.setSignupToken(signupToken);
        companyRepository.save(company);

        String invitationLink = "http://localhost:9191/api/auth/company-signup?email=" + URLEncoder.encode(companyEmail, StandardCharsets.UTF_8) + "&token=" + signupToken;
        String subject = "Invitation to Sign Up";
        String messageBody = "Dear Company, \n\nYou have been invited to sign up on our platform. Please click the link below to complete your registration:\n\n" + invitationLink + "\n\nNote from the admin: " + note;
        EmailDetails emailDetails = new EmailDetails(companyEmail, subject, messageBody);

        emailService.sendEmail(emailDetails);

        return  "Company with " + companyEmail + " onboarded successfully. Email sent to company to complete registration";
    }

    @Override
    public void deactivateUser(String userId) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(()-> new CustomException("User not found with id: " + userId));
        if (user != null) {
            user.setActive(true);
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
            user.setActive(false);
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
        List<ItemMenu> allItemMenus = new ArrayList<>();
        List<Order> allOrders = orderRepository.findAll();

        for (Order order : allOrders) {
            allItemMenus.addAll(order.getItemMenu());
        }

        // Group itemMenus by name and count the orders for each itemMenu
        Map<String, Long> itemMenuOrdersCountMap = allItemMenus.stream()
                .collect(Collectors.groupingBy(ItemMenu::getItemName, Collectors.counting()));

        List<ItemMenuInfoResponse> itemMenuInfoResponses = allItemMenus.stream()
                .map(itemMenu -> {
                    List<String> mealCategories = new ArrayList<>();
                    if (itemMenu.getBreakfast()) mealCategories.add("Breakfast");
                    if (itemMenu.getLunch()) mealCategories.add("Lunch");
                    if (itemMenu.getDinner()) mealCategories.add("Dinner");
                    if(itemMenu.getOthers()) mealCategories.add("Others");

                    return ItemMenuInfoResponse.builder()
                            .itemName(itemMenu.getItemName())
                            .orderCount(itemMenuOrdersCountMap.getOrDefault(itemMenu.getItemName(), 0L))
                            .updatedDate(itemMenu.getUpdatedAt())
                            .mealCategories(mealCategories)
                            .build();
                })
                .collect(Collectors.toList());

        return itemMenuInfoResponses;
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
        // Generate a unique signup token for vendor
        UUID signupToken = UUID.randomUUID();
        return signupToken.toString();
    }
}
