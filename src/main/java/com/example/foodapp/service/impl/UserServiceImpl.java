package com.example.foodapp.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.foodapp.dto.request.*;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.entities.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.*;
import com.example.foodapp.security.JwtService;
import com.example.foodapp.service.EmailService;
import com.example.foodapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final VendorRepository vendorRepository;
    private final AdminRepository adminRepository;
    private final CompanyRepository companyRepository;
    private final Cloudinary cloudinary;
    private final VendorReviewRepository vendorReviewRepository;
    private final ItemMenuRepository itemMenuRepository;
    private final ItemMenuReviewRepository itemMenuReviewRepository;

    //private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Override
    public UserResponse signup(RegistrationRequest request) {

        boolean isExistingStaff = userRepository.existsByEmail(request.getEmail());
        if (!isExistingStaff) {
            throw new CustomException("Contact Admin for signup!!!");
        }

        boolean isExistingVendor = vendorRepository.existsByEmail(request.getEmail());
        if (isExistingVendor) {
            throw new CustomException("Vendor with " + request.getEmail() + " already exists");
        }

        boolean isExistingAdmin = adminRepository.existsByEmail(request.getEmail());
        if (isExistingAdmin) {
            throw new CustomException("User already exist with the email: " + request.getEmail());
        }

        boolean isExistingCompany = companyRepository.existsByCompanyEmail(request.getEmail());
        if (isExistingCompany) {
            throw new CustomException("Company with this email already exist");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new CustomException("Password does not match");
        }

        String verificationToken = generateVerificationToken();
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User existingStaff = userRepository.findByEmail(request.getEmail());


        if(existingStaff.getPassword() != null) {
            throw new CustomException("User already exist!!!");
        }

        existingStaff.setFirstName(request.getFirstName());
        existingStaff.setLastName(request.getLastName());
        existingStaff.setPhone(request.getPhone());
        existingStaff.setPassword(encodedPassword);
        existingStaff.setEnabled(true);

        userRepository.save(existingStaff);

        return UserResponse.builder()
                .userId(existingStaff.getId())
                .firstName(existingStaff.getFirstName())
                .lastName(existingStaff.getLastName())
                .email(existingStaff.getEmail())
                .build();
        }

    public UserResponse updateUserProfile(String firstName, String lastName, String phone, MultipartFile profilePhoto) throws IOException {

        User existingUser = getAuthenticatedUser();

        String publicId = "user_profile_" + existingUser.getId();

        Map uploadResult = cloudinary.uploader().upload(profilePhoto.getBytes(),
                ObjectUtils.asMap(
                        "public_id", publicId,
                        "folder", "images",
                        "overwrite", true,
                        "resource_type", "auto"
                ));
        String imageUrl = uploadResult.get("secure_url").toString();

        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setPhone(phone);
        existingUser.setProfilePictureUrl(imageUrl);

        userRepository.save(existingUser);

        return UserResponse.builder()
                .userId(existingUser.getId())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .email(existingUser.getEmail())
                .profilePictureUrl(imageUrl)
                .build();
    }

    public String changePassword(ChangePasswordRequest request) {
        User existingUser = getAuthenticatedUser();

        if (!passwordEncoder.matches(request.getOldPassword(), existingUser.getPassword())) {
            throw new CustomException("Incorrect old password");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            System.out.println("New password: " + request.getNewPassword());
            System.out.println("Confirm password: " + request.getConfirmNewPassword());
            throw new CustomException("New password and confirm password do not match");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        existingUser.setPassword(encodedPassword);

        userRepository.save(existingUser);

        return "Password changed successfully!!!";
    }

    @Override
    public String verifyAccount(String verificationToken) {
        User user = userRepository.findByVerificationToken(verificationToken);
        if (user != null) {
            user.setEnabled(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return "Account verified successfully. Proceed to login.";
        }
        throw new CustomException("Invalid verification token or email.");
    }

    @Override
    public LoginResponse authenticate(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new CustomException("User with " + email + " does not exist");
        }

        if (!user.getEnabled()) {
            throw new CustomException("User with " + email + " is not enabled");
        }

        if(user.getActive()) {
            throw new CustomException("Account has been locked! Contact support");
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String accessToken = jwtService.generateToken(createAuthentication(user.getEmail(), user.getPassword()));
            String refreshToken = jwtService.generateRefreshToken(createAuthentication(user.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(createAuthentication(user.getEmail(), user.getPassword()));

            return LoginResponse.builder()
                    .email(user.getEmail())
                    .message(user.getRole() + " logged in successfully!!!")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new CustomException("Invalid username or password");
    }

    @Override
    public String forgotPassword(String email) throws IOException {

        /*CustomFileHandler customFileHandler = new CustomFileHandler();
        logger.addHandler(customFileHandler);

        try {*/
            User user = userRepository.findByEmail(email);
            //logger.info("Reset password User --------------->" + user);

            if (user == null) {
                throw new CustomException("User with " + email + " does not exist");
            }

            String resetToken = generateResetToken();
            user.setVerificationToken(resetToken);
            userRepository.save(user);

            sendPasswordResetEmail(email, resetToken);

            return "Password reset link has been sent to your email address!!!.";
        /*}finally {
            logger.removeHandler(customFileHandler);
        }*/
    }

    @Override
    public List<UserDashBoardResponse> getUserDashBoard() {
        List<UserDashBoardResponse> detailsResponses = new ArrayList<>();
        List<Vendor> vendors = vendorRepository.findAll();

        for (Vendor vendor : vendors) {
            UserDashBoardResponse userDashBoardResponse = new UserDashBoardResponse();
            userDashBoardResponse.setId(vendor.getId());
            userDashBoardResponse.setVendorBusinessName(vendor.getBusinessName());
            userDashBoardResponse.setVendorImageUrl(vendor.getImageUrl());

            detailsResponses.add(userDashBoardResponse);
        }
        return detailsResponses;
    }

    public UserResponse viewUserProfile() throws IOException {
        /*/CustomFileHandler customFileHandler = new CustomFileHandler();
        logger.addHandler(customFileHandler);

        try {*/
            User existingUser = getAuthenticatedUser();
            //logger.info("Existing user -----------------> " + existingUser);
            return UserResponse.builder()
                    .userId(existingUser.getId())
                    .firstName(existingUser.getFirstName())
                    .lastName(existingUser.getLastName())
                    .email(existingUser.getEmail())
                    .phone(existingUser.getPhone())
                    .profilePictureUrl(existingUser.getProfilePictureUrl())
                    .build();
        /*}
        finally {
            logger.removeHandler(customFileHandler);
        }*/
    }

    @Override
    public DetailsResponse getVendorDetails(String vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(()-> new CustomException("Vendor not found with id: " + vendorId));

        return DetailsResponse.builder()
                .id(vendor.getId())
                .vendorEmail(vendor.getEmail())
                .businessName(vendor.getBusinessName())
                .address(vendor.getBusinessAddress())
                .contactNumber(vendor.getPhone())
                .itemCategories(vendor.getItemCategory())
                .build();
    }

    @Override
    public VendorReviewResponse addRatingAndReviewByUser(VendorReview vendorReview, String vendorId, ReviewRequest reviewRequest) {

        Integer rating = reviewRequest.getRating();
        String comment = reviewRequest.getComment();

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException("Vendor not found"));

        User user = getAuthenticatedUser();

        List<VendorReview> existingUserVendorReviews = vendorReviewRepository.findByVendorAndUser(vendor, user);
        if (!existingUserVendorReviews.isEmpty()) {
            throw new CustomException("User has already reviewed this vendor!!!");
        }
        vendorReview.setRating(rating);
        vendorReview.setComment(comment);
        vendorReview.setVendor(vendor);
        vendorReview.setUser(user);
        vendorReviewRepository.save(vendorReview);

        List<VendorReview> vendorReviews = vendor.getVendorReviews();
        double sumRatings = vendorReviews.stream().mapToDouble(VendorReview::getRating).sum();
        Double averageRating = vendorReviews.isEmpty() ? 0.0 : sumRatings / vendorReviews.size();

        vendor.setAverageRating(averageRating);
        vendor.setTotalRatings((long) vendorReviews.size());

        vendorRepository.save(vendor);

        return VendorReviewResponse.builder()
                .id(vendor.getId())
                .businessName(vendor.getBusinessName())
                .averageRating(vendor.getAverageRating())
                .build();
    }


@Override
    public ItemMenuReviewResponse addRatingAndReviewToItemMenuByUser(ItemMenuReview itemMenuReview, String itemMenuId, ReviewRequest reviewRequest) {

        Integer rating = reviewRequest.getRating();
        String comment = reviewRequest.getComment();

        ItemMenu itemMenu = itemMenuRepository.findById(itemMenuId)
                .orElseThrow(() -> new CustomException("Item menu not found"));

        User user = getAuthenticatedUser();

        List<ItemMenuReview> existingUserItemMenuReviews = itemMenuReviewRepository.findByItemMenuAndUser(itemMenu, user);
        if (!existingUserItemMenuReviews.isEmpty()) {
            throw new CustomException("User has already reviewed this item!!!");
        }

        itemMenuReview.setRating(rating);
        itemMenuReview.setComment(comment);
        itemMenuReview.setItemMenu(itemMenu);
        itemMenuReview.setUser(user);
        itemMenuReviewRepository.save(itemMenuReview);

        List<ItemMenuReview> itemMenuReviews = itemMenu.getItemMenuReviews();
        double sumRatings = itemMenuReviews.stream().mapToDouble(ItemMenuReview::getRating).sum();
        Double averageRating = itemMenuReviews.isEmpty() ? 0.0 : sumRatings / itemMenuReviews.size();

        itemMenu.setAverageRating(averageRating);
        itemMenu.setTotalRatings((long) itemMenuReviews.size());

        itemMenuRepository.save(itemMenu);

        return ItemMenuReviewResponse.builder()
                .id(itemMenu.getItemId())
                .imageUrl(itemMenu.getImageUrl())
                .averageRating(itemMenu.getAverageRating())
                .build();
    }


    private void sendVerificationEmail(String recipient, String verificationToken) throws IOException {
        String verificationLink = "http://localhost:9191/api/auth/verify?token=" + verificationToken;
        String subject = "Account Verification";
        String messageBody = "Please click on the link below to verify your account:\n" + verificationLink;
        EmailDetails emailDetails = new EmailDetails(recipient, subject, messageBody);
        emailService.sendEmail(emailDetails);
    }

    private void sendPasswordResetEmail(String recipient, String resetCode) throws IOException {
        String subject = "Password Reset";
        String messageBody = "Your password reset code is :" + resetCode;
        EmailDetails emailDetails = new EmailDetails(recipient, subject, messageBody);
        emailService.sendEmail(emailDetails);
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    private String generateResetToken() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000);
        return String.format("%06d", randomNumber);
    }

    private Authentication createAuthentication(String username, String password) {
        return new UsernamePasswordAuthenticationToken(username, password);
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
}