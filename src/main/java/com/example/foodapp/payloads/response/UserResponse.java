package com.example.foodapp.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String profilePictureUrl;
}
