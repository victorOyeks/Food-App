package com.example.foodapp.repository;

import com.example.foodapp.entities.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplementRepository extends JpaRepository <Supplement, String> {

    List<Supplement> findByVendorId (String vendorId);
    Supplement findByVendorIdAndAndSupplementId (String vendorId, String supplementId);

    Supplement findBySupplementId(String supplementId);
}
