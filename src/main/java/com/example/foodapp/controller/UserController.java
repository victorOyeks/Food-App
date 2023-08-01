package com.example.foodapp.controller;

import com.example.foodapp.dto.response.*;
import com.example.foodapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}