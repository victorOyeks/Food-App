package com.example.foodapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    private ItemCategory itemCategory;
    @ElementCollection
    private List<String> availableSupplements;
    @JsonIgnore
    @OneToMany(mappedBy = "itemMenu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Supplement> selectedSupplements = new ArrayList<>();
    private Double averageRating;
    private Long totalRatings;
    @JsonIgnore
    @OneToMany(mappedBy = "itemMenu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMenuReview> itemMenuReviews = new ArrayList<>();
}