package com.example.foodapp.controller;

import com.example.foodapp.dto.request.*;
import com.example.foodapp.dto.request.ResetEmail;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.exception.ResourceNotFoundException;
import com.example.foodapp.service.CompanyService;
import com.example.foodapp.service.UserService;
import com.example.foodapp.service.VendorService;
import com.example.foodapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final VendorService vendorService;
    private final AuthService authService;
    private final CompanyService companyService;

    @GetMapping("verify")
    public ResponseEntity<ApiResponse<String>> verifyAccount(@RequestParam("token") String verificationToken) {
        ApiResponse<String> apiResponse = new ApiResponse<>(userService.verifyAccount(verificationToken));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("authenticate")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(authService.authenticate(loginRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("vendor-signup")
    public ResponseEntity<?> vendorSignup(@RequestParam("email") String email, @RequestParam("token") String token) {
        try {
            String response = authService.vendorAdminSignup(email, token);
            return ResponseEntity.ok("Registration complete for email: " + email);
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("vendor-signup")
    public ResponseEntity<ApiResponse<BusinessRegistrationResponse>> vendorSignup(@RequestBody VendorRegistrationRequest request) {
        ApiResponse<BusinessRegistrationResponse> apiResponse = new ApiResponse<>(vendorService.vendorSignup(request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @GetMapping("company-signup")
    public ResponseEntity<?> companySignup (@RequestParam("email") String email, @RequestParam("token") String token) {
        try {
            String response = authService.companyAdminSignup(email, token);
            return ResponseEntity.ok("Registration complete for email: " + email);
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("company-signup")
    public ResponseEntity<ApiResponse<BusinessRegistrationResponse>> companySignup (@RequestBody CompanyRegistrationRequest request) throws IOException {
        ApiResponse<BusinessRegistrationResponse> apiResponse = new ApiResponse<>(companyService.companySignup(request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("staff-signup")
    public ResponseEntity<?> staffSignup (@RequestParam("token") String token) {
        try {
            String response = authService.staffAdminSignup(token);
            return ResponseEntity.ok("Registration complete!!!");
        } catch (CustomException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("staff-signup")
    public ResponseEntity<ApiResponse<UserResponse>> staffSignup (@RequestBody RegistrationRequest request) throws IOException {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(userService.signup(request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ResetEmail resetEmail) {
        ApiResponse<String> apiResponse;

        try {
            String forgotPasswordResponse = authService.forgotPassword(resetEmail);
            apiResponse = new ApiResponse<>(forgotPasswordResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ResourceNotFoundException exception) {
            apiResponse = new ApiResponse<>(exception.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        ApiResponse<String> apiResponse;

        try {
            String resetPasswordResponse = authService.resetPassword(resetPasswordRequest);
            apiResponse = new ApiResponse<>(resetPasswordResponse);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (CustomException exception) {
            apiResponse = new ApiResponse<>(exception.getMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}
