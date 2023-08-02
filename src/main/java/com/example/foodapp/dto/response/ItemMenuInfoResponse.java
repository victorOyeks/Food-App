package com.example.foodapp.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemMenuInfoResponse {
    private String itemName;
    private Long orderCount;
    private LocalDateTime updatedDate;
    private List<String> mealCategories;
}
