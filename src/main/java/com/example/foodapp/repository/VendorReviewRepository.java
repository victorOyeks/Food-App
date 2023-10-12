package com.example.foodapp.repository;

import com.example.foodapp.entities.Company;
import com.example.foodapp.entities.VendorReview;
import com.example.foodapp.entities.User;
import com.example.foodapp.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorReviewRepository extends JpaRepository<VendorReview, String> {
    List<VendorReview> findByVendorAndUser(Vendor vendor, User user);
    @Query("SELECT r FROM VendorReview r WHERE r.vendor = :vendor AND r.user = :user")
    List<VendorReview> findByVendorAndUserOrCompany(Vendor vendor, User user);
}

