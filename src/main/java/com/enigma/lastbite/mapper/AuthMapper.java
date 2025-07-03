package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.AdminRegisterRequest;
import com.enigma.lastbite.dto.request.CustomerRegisterRequest;
import com.enigma.lastbite.dto.request.SellerRegisterRequest;
import com.enigma.lastbite.entity.Role;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuthMapper {
    public static User toUser(CustomerRegisterRequest request, PasswordEncoder passwordEncoder, Role role) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static User toUser(AdminRegisterRequest request, PasswordEncoder passwordEncoder, Role role) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static User toUser(SellerRegisterRequest request, PasswordEncoder passwordEncoder, Role role) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static SellerProfile toSellerProfile(SellerRegisterRequest request, User user) {
        SellerProfile seller = new SellerProfile();
        seller.setUser(user);
        seller.setStoreName(request.getStoreName());
        seller.setStoreDescription(request.getStoreDescription());
        seller.setAddress(request.getAddress());
        seller.setLatitude(request.getLatitude());
        seller.setLongitude(request.getLongitude());
        seller.setStatus(UserStatus.INACTIVE);
        seller.setBalance(BigDecimal.ZERO);
        seller.setCreatedAt(LocalDateTime.now());
        seller.setUpdatedAt(LocalDateTime.now());
        return seller;
    }
}
