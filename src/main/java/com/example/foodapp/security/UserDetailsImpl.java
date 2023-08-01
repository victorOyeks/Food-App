package com.example.foodapp.security;

import com.example.foodapp.constant.ROLE;
import com.example.foodapp.entities.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    private final User user;
    private final Vendor vendor;
    private final Admin admin;
    private final Company company;

    public UserDetailsImpl(User user, Vendor vendor, Admin admin, Company company) {
        this.user = user;
        this.admin = admin;
        this.vendor = vendor;
        this.company = company;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<ROLE> ROLES = new ArrayList<>();

        if (user != null && user.getRole() != null) {
            ROLES.add(user.getRole());
        }
        if (vendor != null && vendor.getRole() != null) {
            ROLES.add(vendor.getRole());
        }
        if (admin != null && admin.getRole() != null) {
            ROLES.add(admin.getRole());
        }
        if (company != null && company.getRole() != null) {
            ROLES.add(company.getRole());
        }

        List<GrantedAuthority> authorities = ROLES.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return authorities;
    }

    @Override
    public String getPassword() {
        if (user != null) {
            return user.getPassword();
        }
        if (vendor != null) {
            return vendor.getPassword();
        }
        if (admin != null) {
            return admin.getPassword();
        }
        if (company != null) {
            return company.getPassword();
        }
        return null;
    }

    @Override
    public String getUsername() {
        if (user != null) {
            return user.getEmail();
        }
        if (vendor != null) {
            return vendor.getEmail();
        }
        if (admin != null) {
            return admin.getEmail();
        }
        if (company != null) {
            return company.getCompanyEmail();
        }
        return null;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
