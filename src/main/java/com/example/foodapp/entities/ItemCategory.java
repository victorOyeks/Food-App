package com.example.foodapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "item_category")
public class ItemCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String categoryId;
    private String categoryName;
    @JsonIgnore
    @JsonIgnoreProperties("itemCategory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
//    @JsonIgnore
    @OneToMany(mappedBy = "itemCategory", cascade = CascadeType.ALL)
    private List<ItemMenu> itemMenus = new ArrayList<>();
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}