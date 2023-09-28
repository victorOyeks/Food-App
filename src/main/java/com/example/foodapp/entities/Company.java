package com.example.foodapp.entities;

import com.example.foodapp.constant.CompanySize;
import com.example.foodapp.constant.ROLE;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
//@ToString
@Entity
@Table
public class Company  {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private String id;
    private String companyEmail;
    private String phoneNumber;
    private String companyName;
    private String password;
    private String companyAddress;
    private String domainName;
    private CompanySize companySize;
    private String imageUrl;
    private ROLE role;
    private Boolean enabled;
    private Boolean active;
    private String signupToken;
    private String verificationToken;
    private BigDecimal priceLimit;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnoreProperties("company")
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> userList = new ArrayList<>();

    @JsonIgnoreProperties("company")
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Order> orderList = new ArrayList<>();

    /*@JsonIgnoreProperties("vendor")
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vendor> vendors = new ArrayList<>();
     */

    @JsonIgnoreProperties("vendor")
    @ManyToMany
    @JoinTable(
            name = "company_vendor",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "vendor_id")
    )
    private List<Vendor> vendors = new ArrayList<>();

/*    public void generateId(){
        String namePrefix = companyName.substring(0, Math.min(3, companyName.length())).toUpperCase();
        Random random = new Random();
        String randomNumbers = String.format("%4d", random.nextInt(10000));
        this.id = namePrefix + "ADMIN" + randomNumbers;
    }
 */
}