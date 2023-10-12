package com.example.foodapp.repository;

import com.example.foodapp.constant.ROLE;
import com.example.foodapp.entities.Company;
import com.example.foodapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, String> {
    User findByEmail (String staffEmail);
    Boolean existsByEmail (String email);
    User findByVerificationToken (String token);
    List<User> findByCompany(Company company);
    @Query("SELECT COUNT(u) FROM User u WHERE u.company = :company AND u.role = :role")
    Integer countByCompanyAndRole(@Param("company") Company company, @Param("role") ROLE role);

}
