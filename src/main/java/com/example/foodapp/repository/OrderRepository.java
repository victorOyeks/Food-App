package com.example.foodapp.repository;

import com.example.foodapp.entities.Order;
import com.example.foodapp.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository <Order, String> {
    List<Order> findOrdersByUserId (String userId);
    Order findOrderByOrderIdAndUserId (String orderId, String userId);
    List<Order> findOrdersByCompanyId (String companyId);
    Order findOrderByOrderIdAndCompanyId (String orderId, String companyId);
    @Query("SELECT o FROM Order o JOIN o.itemMenu i JOIN i.itemCategory c WHERE c.vendor = ?1")
    List<Order> findOrdersByVendor (Vendor vendor);

    @Query("SELECT o FROM Order o JOIN o.itemMenu i JOIN i.itemCategory c WHERE c.vendor = ?1 and o.orderId = ?2")
    Order findAnOrdersByVendor (Vendor vendor, String orderId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.deliveryStatus = com.example.foodapp.constant.DeliveryStatus.PENDING")
    Order findOpenOrderByUser(String userId);

    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.deliveryStatus = com.example.foodapp.constant.DeliveryStatus.PENDING")
    Order findOpenOrderByCompany(String companyId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.paymentStatus =com.example.foodapp.constant.PaymentStatus.PENDING")
    List<Order> findPendingOrdersByUserId(String userId);

    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.paymentStatus =com.example.foodapp.constant.PaymentStatus.PENDING")
    List<Order> findPendingOrdersByCompanyId(String companyId);

    Optional<Order> findByOrderIdAndUserId(String orderId, String userId);

    Optional<Order> findByOrderIdAndCompanyId(String orderId, String companyId);

//    @Query("SELECT o FROM Order o join o.itemMenu i JOIN i.itemCategory c WHERE c.vendor.id = ?1")
//    Order findOrderByOrderIdAndVendorId(String orderId, String vendorId);

    @Query("SELECT o FROM Order o JOIN o.itemMenu i JOIN i.itemCategory c WHERE c.vendor.id = ?1 AND o.orderId = ?2")
    Order findOrderByOrderIdAndVendorId(String vendorId, String orderId);

}
