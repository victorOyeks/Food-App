package com.example.foodapp.repository;

import com.example.foodapp.entities.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCategoryRepository extends JpaRepository <ItemCategory, String> {
    Optional<ItemCategory> findByVendorIdAndCategoryId (String vendorId, String categoryId);
    List<ItemCategory> findByVendorId (String vendorId);
}
