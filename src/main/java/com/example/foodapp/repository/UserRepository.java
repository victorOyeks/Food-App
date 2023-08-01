package com.example.foodapp.repository;

import com.example.foodapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, String> {
    User findByEmail (String staffEmail);
    Boolean existsByEmail (String email);
//    User findByVerificationTokenAndEmail (String verificationToken, String email);
    User findByVerificationToken (String token);
//    User findByROLE (ROLE ROLE);
//    User findByEmailAndROLE (String email, ROLE ROLE);
}
