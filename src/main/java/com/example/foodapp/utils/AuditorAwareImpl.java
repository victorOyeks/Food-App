/*package com.example.foodapp.utils;

import com.example.foodapp.entities.Admin;
import com.example.foodapp.entities.Company;
import com.example.foodapp.entities.User;
import com.example.foodapp.entities.Vendor;
import com.example.foodapp.exception.CustomException;
import com.example.foodapp.repository.AdminRepository;
import com.example.foodapp.repository.CompanyRepository;
import com.example.foodapp.repository.UserRepository;
import com.example.foodapp.repository.VendorRepository;
import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final CompanyRepository companyRepository;
    private final AdminRepository adminRepository;

    @Override
    public Optional<String> getCurrentAuditor() {
        String userRole = getAuthenticatedUserRole();
        return Optional.of(userRole);
    }

    private String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        // Retrieve the user entity based on the authentication's role
        Vendor vendor = vendorRepository.findByEmail(userEmail);
        if (vendor != null) {
            return vendor.getRole().toString(); // Adjust this to retrieve the actual role
        }

        User user = userRepository.findByEmail(userEmail);
        if (user != null) {
            return user.getRole().toString(); // Adjust this to retrieve the actual role
        }

        Company company = companyRepository.findByCompanyEmail(userEmail);
        if (company != null) {
            return company.getRole().toString(); // Adjust this to retrieve the actual role
        }

        Admin admin = adminRepository.findByEmail(userEmail);
        if (admin != null) {
            return admin.getRole().toString(); // Adjust this to retrieve the actual role
        }

        throw new CustomException("Entity not found");
    }

    /*private String getAuthenticatedUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new CustomException("Role not found"))
                .getAuthority();
    }*/

    /*private Object getAuthenticatedEntity(Class<?> entityType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        if (entityType.equals(Vendor.class)) {
            Vendor vendor = vendorRepository.findByEmail(userEmail);
            if (vendor == null) {
                throw new CustomException("Vendor not found");
            }
            return vendor;
        } else if (entityType.equals(User.class)) {
            User user = userRepository.findByEmail(userEmail);
            if (user == null) {
                throw new CustomException("User not found");
            }
            return user;
        } else if (entityType.equals(Company.class)) {
            Company company = companyRepository.findByCompanyEmail(userEmail);
            if (company == null) {
                throw new CustomException("Company not found");
            }
            return company;
        } else if (entityType.equals(Admin.class)) {
            Admin admin = adminRepository.findByEmail(userEmail);
            if (admin == null) {
                throw new CustomException("Admin not found");
            }
            return admin;
        }

        throw new CustomException("Entity type not supported");
    }

}
*/