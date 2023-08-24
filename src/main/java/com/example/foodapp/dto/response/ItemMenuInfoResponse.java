package com.example.foodapp.dto.response;

import com.example.foodapp.entities.ItemCategory;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemMenuInfoResponse {
    private String itemName;
    private Long orderCount;
    private LocalDateTime updatedDate;
    private String itemCategory;
}
