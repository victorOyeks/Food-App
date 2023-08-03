package com.example.foodapp.entities;

import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.constant.ROLE;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table
public class Company  {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;
    private String companyEmail;
    private String phoneNumber;
    private String companyName;
    private String password;
    private String companyAddress;
    private CompanySize companySize;
    private String imageUrl;
    private ROLE role;
    private Boolean enabled;
    private Boolean deactivated;
    private String signupToken;
    private String verificationToken;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @JsonIgnoreProperties("company")
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> userList = new ArrayList<>();
    @JsonIgnoreProperties("company")
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Order> orderList = new ArrayList<>();
}
