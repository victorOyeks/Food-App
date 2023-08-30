package com.example.foodapp.entities;

import com.example.foodapp.constant.SupplementCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private SupplementCategory supplementCategory;
    @JsonIgnore
    @JsonIgnoreProperties("supplement")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
}
