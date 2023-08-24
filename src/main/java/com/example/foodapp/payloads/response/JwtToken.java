package com.example.foodapp.payloads.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JwtToken {
    private String accessToken;
    private String refreshToken;

}