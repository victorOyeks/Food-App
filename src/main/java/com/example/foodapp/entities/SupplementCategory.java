package com.example.foodapp.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "supplement_category)")
public class SupplementCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String name;
    @OneToMany(mappedBy = "supplementCategory", cascade = CascadeType.ALL)
    List<Supplement> supplements = new ArrayList<>();
}
