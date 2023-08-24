package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemMenusInCategoriesResponse {
        private String itemName;
        private Long totalSales;
        private Double ratingByOrder;
        private String image;
}
