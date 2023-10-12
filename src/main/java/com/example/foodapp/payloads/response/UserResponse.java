package com.example.foodapp.payloads.response;

import com.example.foodapp.entities.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String userCompany;
    private String position;
    private Boolean activeStatus;
    private LocalDateTime createdAt;
    private BigDecimal totalSpending;
    private BigDecimal lastOrder;
    private String profilePictureUrl;
}
