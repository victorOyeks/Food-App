package com.example.foodapp.repository;

import com.example.foodapp.entities.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository <Vendor, String> {
    Boolean existsByEmail (String vendorEmail);
    Vendor findByEmailAndSignupToken (String email, String token);
    Vendor findByEmail (String email);
    boolean existsById (String vendorId);
    Vendor findByVerificationToken (String token);
}
