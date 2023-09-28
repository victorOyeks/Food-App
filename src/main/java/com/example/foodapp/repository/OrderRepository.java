package com.example.foodapp.repository;

import com.example.foodapp.entities.Order;
import com.example.foodapp.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository <Order, String> {
    List<Order> findOrdersByUserId (String userId);
    Order findOrderByOrderIdAndUserId (String orderId, String userId);
    List<Order> findOrdersByCompanyId (String companyId);
    Order findOrderByOrderIdAndCompanyId (String orderId, String companyId);

//    @Query("SELECT o FROM Order o JOIN o.itemMenus i JOIN i.itemCategory c WHERE c.vendor = ?1")
//    List<Order> findOrdersByVendor (Vendor vendor);

    @Query("SELECT o FROM Order o WHERE o.vendor = ?1")
    List<Order> findOrdersByVendor(Vendor vendor);

//    @Query("SELECT o FROM Order o JOIN o.company c WHERE c.id = ?1 AND o.orderId = ?2")
    Order findByOrderIdAndVendorId(String orderId, String vendorId);


//    @Query("SELECT o FROM Order o JOIN o.itemMenus i JOIN i.itemCategory c WHERE c.vendor = ?1 and o.orderId = ?2")
//    Order findAnOrdersByVendor (Vendor vendor, String orderId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.submitStatus = com.example.foodapp.constant.SubmitStatus.PENDING")
    Order findOpenOrderByUser(String userId);

    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.submitStatus = com.example.foodapp.constant.SubmitStatus.PENDING")
    Order findOpenOrderByCompany(String companyId);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.submitStatus =com.example.foodapp.constant.SubmitStatus.PENDING")
    List<Order> findPendingOrdersByUserId(String userId);

    @Query("SELECT o FROM Order o WHERE o.company.id = :companyId AND o.submitStatus =com.example.foodapp.constant.SubmitStatus.PENDING")
    List<Order> findPendingOrdersByCompanyId(String companyId);

    Optional<Order> findByOrderIdAndUserId(String orderId, String userId);

    Optional<Order> findByOrderIdAndCompanyId(String orderId, String companyId);

    Order findByOrderId(String orderId);

    // Total orders received by the vendor for a specific time frame
    @Query("SELECT COUNT(o) FROM Order o WHERE o.vendor = :vendor AND o.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersByVendorAndCreatedAtBetween(Vendor vendor, LocalDateTime startDate, LocalDateTime endDate);

    // Total amount of sales for the vendor for a specific time frame
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.vendor = :vendor AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByVendorAndCreatedAtBetween(Vendor vendor, LocalDateTime startDate, LocalDateTime endDate);

//    @Query("SELECT o FROM Order o join o.itemMenu i JOIN i.itemCategory c WHERE c.vendor.id = ?1")
//    Order findOrderByOrderIdAndVendorId(String orderId, String vendorId);
}
