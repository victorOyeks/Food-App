package com.example.foodapp.controller;

import com.example.foodapp.dto.request.StaffInvitation;
import com.example.foodapp.dto.response.ApiResponse;
import com.example.foodapp.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/company/")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("invite-staff")
    public ResponseEntity<ApiResponse<String>> inviteStaff(@RequestBody StaffInvitation staffInvitation) throws IOException {
        log.info("entering the controller");
        ApiResponse<String> apiResponse = new ApiResponse<>(companyService.inviteStaff(staffInvitation));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }
}
