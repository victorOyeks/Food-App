package com.example.foodapp.repository;

import com.example.foodapp.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemMenuReviewRepository extends JpaRepository <ItemMenuReview, String> {
    List<ItemMenuReview> findByItemMenuAndUser(ItemMenu itemMenu, User user);
    List<ItemMenuReview> findByItemMenuAndCompany(ItemMenu itemMenu, Company company);
}
