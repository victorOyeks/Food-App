package com.example.foodapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ItemMenuReview {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String reviewId;
        private Integer rating;
        private String comment;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "itemMenu_id")
        private ItemMenu itemMenu;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "company_id")
        private Company company;

        @CreationTimestamp
        private LocalDateTime createdAt;

        @UpdateTimestamp
        private LocalDateTime updatedAt;
    }
