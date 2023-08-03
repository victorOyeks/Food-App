package com.example.foodapp.controller;

import com.example.foodapp.dto.request.ChangePasswordRequest;
import com.example.foodapp.dto.response.*;
import com.example.foodapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("dashboard")
    public ResponseEntity<ApiResponse<List<UserDashBoardResponse>>> dashboard() {
        ApiResponse<List<UserDashBoardResponse>> apiResponse = new ApiResponse<>(userService.getUserDashBoard());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("update-profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@RequestParam String firstName,
                                                                   @RequestParam String lastName,
                                                                   @RequestParam String phone,
                                                                   @RequestParam MultipartFile profilePhoto) throws IOException {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>(userService.updateUserProfile(firstName, lastName, phone, profilePhoto));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    @PutMapping("change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ApiResponse<String> apiResponse = new ApiResponse<>(userService.changePassword(changePasswordRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}