package com.example.foodapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "item_menu")
public class ItemMenu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String itemId;
    private String itemName;
    private BigDecimal itemPrice;
    private String imageUrl;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    private ItemCategory itemCategory;

}