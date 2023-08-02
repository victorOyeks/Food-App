package com.example.foodapp.dto.response;

import jakarta.persistence.PostLoad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ItemMenuResponse {
    private String itemName;
    private BigDecimal itemPrice;
    private String imageUrl;
    private Boolean breakfast;
    private Boolean lunch;
    private Boolean dinner;
    private LocalDateTime updatedAt;
    private String categoryName;
}
