package com.example.foodapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToMany(mappedBy = "selectedSupplements", fetch = FetchType.LAZY)
    private List<ItemMenu> itemMenus = new ArrayList<>();

    public Supplement (String supplementId) {
        this.supplementId = supplementId;
    }
}
