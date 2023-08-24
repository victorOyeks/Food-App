package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemMenusInAllCategoriesResponse {
        private String itemName;
        private Long totalSales;
        private LocalDateTime ratingByOrder;
        private String image;
}
