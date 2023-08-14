package com.example.foodapp.repository;

import com.example.foodapp.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository <Review, String> {
}
