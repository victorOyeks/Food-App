package com.example.foodapp.repository;

import com.example.foodapp.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository <Company, String> {
    Company findByCompanyEmailAndSignupToken (String email, String token);
    boolean existsByCompanyEmail (String email);
    Company findByCompanyEmail (String email);
    Company findByVerificationToken(String resetToken);
}
