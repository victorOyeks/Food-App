package com.example.foodapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "order_item_supplements")
public class OrderItemSupplement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderItemSupplementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplement_id")
    private Supplement supplement;


    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    private Integer quantity;
}
