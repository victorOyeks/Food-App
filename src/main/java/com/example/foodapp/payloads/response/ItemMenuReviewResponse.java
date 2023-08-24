package com.example.foodapp.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ItemMenuReviewResponse {
    private String id;
    private String itemMenu;
    private String imageUrl;
    private Double averageRating;
}