package com.example.foodapp.repository;

import com.example.foodapp.entities.ItemMenu;
import com.example.foodapp.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemMenuRepository extends JpaRepository <ItemMenu, String> {

    ItemMenu findByItemId (String id);

    @Query("SELECT i FROM ItemMenu i WHERE i.itemCategory.vendor.id = :vendorId")
    List<ItemMenu> findAllByVendorId(@Param("vendorId") String vendorId);

    @Query("SELECT i FROM ItemMenu i WHERE i.itemId = :itemId AND i.itemCategory.vendor.id = :vendorId")
    Optional<ItemMenu> findByItemIdAndVendorId(String itemId, String vendorId);

    @Query("SELECT i FROM ItemMenu i WHERE i.itemCategory.categoryId = :categoryId AND i.itemCategory.vendor.id = :vendorId")
    List<ItemMenu> findAllByCategoryIdAndVendorId(String categoryId, String vendorId);

}
