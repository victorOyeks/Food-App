package com.example.foodapp.repository;

import com.example.foodapp.constant.ROLE;
import com.example.foodapp.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository <Admin, String> {
    Admin findByEmail (String email);
    Boolean existsByEmail (String email);
    Admin findByRole (ROLE ROLE);
    Admin findByEmailAndRole (String email, ROLE ROLE);
    Admin findByVerificationToken(String resetToken);
//    Admin findByEmailAndSignupToken (String email, String token);
}
