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
//@ToString
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
    private String businessAddress;
    private GeoLocation coordinates;
    private String mapUri;
    private String imageUrl;
    private Boolean active;
    private String signupToken;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean storeStatus;
    @JsonIgnoreProperties("vendor")
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCategory> itemCategory = new ArrayList<>();

    @JsonIgnoreProperties("vendor")
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Supplement> supplements = new ArrayList<>();

    @JsonIgnoreProperties("vendor")
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    private Double averageRating;
    private Long totalRatings;
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VendorReview> vendorReviews = new ArrayList<>();

    @ManyToMany (mappedBy = "vendors", cascade = CascadeType.ALL)
    private List<Company> companies = new ArrayList<>();
}