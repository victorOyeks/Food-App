package com.example.foodapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Supplement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String supplementId;
    private String supplementName;
    private BigDecimal supplementPrice;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplement_category_id")
    private SupplementCategory supplementCategory;
}
