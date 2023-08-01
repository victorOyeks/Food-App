package com.example.foodapp.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
//@Getter
//@Setter
@Builder
@Data
//@Entity
//@Table(name = "jwt_token")
public class JwtToken {
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private String id;
    private String accessToken;
    private String refreshToken;
//    private boolean isExpired;
//    private boolean isRevoked;
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "users_id")
//    private User user;
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "admin_id")
//    private Admin admin;
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "vendor_id")
//    private Vendor vendor;

}