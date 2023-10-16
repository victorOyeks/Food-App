package com.example.foodapp.repository;

import com.example.foodapp.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository <OrderItem, String > {
}
