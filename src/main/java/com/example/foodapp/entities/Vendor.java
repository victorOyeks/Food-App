package com.example.foodapp.entities;

import com.example.foodapp.constant.ROLE;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "vendor")
public class Vendor{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private ROLE role;
    private String verificationToken;
    private Boolean enabled;
    private String businessName;
    private String domainName;
    private String businessAddress;
    private String imageUrl;
    private Boolean deactivated;
    private String signupToken;
    @JsonIgnoreProperties("vendor")
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCategory> itemCategory = new ArrayList<>();
}