package com.example.foodapp.repository;

import com.example.foodapp.entities.OrderItemSupplement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemSupplementRepository extends JpaRepository <OrderItemSupplement, String > {
}
