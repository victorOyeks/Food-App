package com.example.foodapp.entities;

import com.example.foodapp.constant.DeliveryStatus;
import com.example.foodapp.constant.SubmitStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ElementCollection
    @CollectionTable(name = "order_item_menus", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyJoinColumn(name = "item_menu_id") // Map key column for itemMenu IDs
    @Column(name = "quantity")
    private Map<String, Integer> itemMenus;

    @ElementCollection
    @CollectionTable(name = "order_supplements", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyJoinColumn(name = "supplement_id")
    @Column(name = "quantity")
    private Map<String, Integer> supplements;

    private BigDecimal totalAmount;

    private SubmitStatus submitStatus;

    private DeliveryStatus deliveryStatus;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}