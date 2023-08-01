package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDashBoardResponse {
    private String id;
    private String vendorBusinessName;
    private String vendorImageUrl;
}
