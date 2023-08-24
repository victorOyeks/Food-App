package com.example.foodapp.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private String confirmPassword;
}
