package com.example.foodapp.payloads.response;

import com.example.foodapp.entities.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DetailsResponse {
    private String id;
    private String vendorEmail;
    private String businessName;
    private String address;
    private String contactNumber;
    private LocalDateTime lastAccessed;
    private Long totalRatings;
    private Double averageRating;
    private Boolean active;
    private List<ItemCategory> itemCategories;
}
