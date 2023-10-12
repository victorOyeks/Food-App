package com.example.foodapp.repository;

import com.example.foodapp.entities.Company;
import com.example.foodapp.entities.Order;
import com.example.foodapp.entities.User;
import com.example.foodapp.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository <Order, String> {
    List<Order> findOrdersByUserId (String userId);
    Order findOrderByOrderIdAndUserId (String orderId, String userId);
    Order findOrderByOrderIdAndCompanyId (String orderId, String companyId);
    @Query("SELECT o FROM Order o WHERE o.vendor = ?1")
    List<Order> findOrdersByVendor(Vendor vendor);
    Order findByOrderIdAndVendorId(String orderId, String vendorId);
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
    // Total orders received by the vendor
    @Query("SELECT COUNT(o) FROM Order o WHERE o.vendor = :vendor")
    Long totalCountOrdersByVendor(Vendor vendor);
    // Total orders received by the vendor for a specific time frame
    @Query("SELECT COUNT(o) FROM Order o WHERE o.vendor = :vendor AND o.createdAt BETWEEN :startDate AND :endDate")
    Long countOrdersByVendorAndCreatedAtBetween(Vendor vendor, LocalDateTime startDate, LocalDateTime endDate);
    // Total amount of sales for the vendor
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.vendor = :vendor")
    BigDecimal totalSumTotalAmountByVendor(Vendor vendor);

    // total amount spent by user
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user = :user")
    BigDecimal totalSpendingByUser(User user);

    @Query("SELECT o.totalAmount FROM Order o WHERE o.user = :user ORDER BY o.createdAt DESC LIMIT 1 ")
    BigDecimal lastOrderByUser (User user);

    // Total amount of sales for the vendor for a specific time frame
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.vendor = :vendor AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByVendorAndCreatedAtBetween(Vendor vendor, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByUserCompany(Company company);
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.company = :company AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByUserCompanyAndCreatedAtBetween(Company company, LocalDateTime startDate, LocalDateTime endDate);
}
