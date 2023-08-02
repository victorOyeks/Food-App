package com.example.foodapp.repository;

import com.example.foodapp.entities.Order;
import com.example.foodapp.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository <Order, String> {
    List<Order> findOrdersByUserId (String userId);
    List<Order> findOrdersByCompanyId (String companyId);

    @Query("SELECT o FROM Order o JOIN o.itemMenu i JOIN i.itemCategory c WHERE c.vendor = ?1")
    List<Order> findOrdersByVendor (Vendor vendor);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.deliveryStatus = com.example.foodapp.constant.DeliveryStatus.PENDING")
    Order findOpenOrderByUser(String userId);
}
