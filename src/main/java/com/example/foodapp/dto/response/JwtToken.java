package com.example.foodapp.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JwtToken {
    private String accessToken;
    private String refreshToken;

}