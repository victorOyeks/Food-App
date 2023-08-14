package com.example.foodapp.repository;

import com.example.foodapp.entities.Company;
import com.example.foodapp.entities.Review;
import com.example.foodapp.entities.User;
import com.example.foodapp.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByVendorAndUser(Vendor vendor, User user);
}

