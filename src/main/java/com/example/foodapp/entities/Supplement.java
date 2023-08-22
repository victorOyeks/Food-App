package com.example.foodapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public Supplement(String supplementName, BigDecimal supplementPrice) {
        this.supplementName = supplementName;
        this.supplementPrice = supplementPrice;
    }
}
