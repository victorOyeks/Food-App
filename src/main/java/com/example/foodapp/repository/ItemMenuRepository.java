package com.example.foodapp.repository;

import com.example.foodapp.entities.ItemMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemMenuRepository extends JpaRepository <ItemMenu, String> {

    ItemMenu findByItemId (String id);

    @Query("SELECT i FROM ItemMenu i WHERE i.itemId = :itemId AND i.itemCategory.vendor.id = :vendorId")
    Optional<ItemMenu> findByItemIdAndVendorId(String itemId, String vendorId);
}
