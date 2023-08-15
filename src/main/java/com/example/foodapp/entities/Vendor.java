package com.example.foodapp.entities;

import com.example.foodapp.constant.ROLE;
import com.example.foodapp.utils.geoLocation.GeoLocation;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
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
    private GeoLocation coordinates;
    private String imageUrl;
    private Boolean active;
    private String signupToken;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    /*
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String lastModifiedBy;
    */
    @JsonIgnoreProperties("vendor")
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCategory> itemCategory = new ArrayList<>();
    private Double averageRating;
    private Long totalRatings;
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

}