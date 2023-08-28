package com.example.foodapp.repository;

import com.example.foodapp.entities.Supplement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplementRepository extends JpaRepository <Supplement, String> {
}
