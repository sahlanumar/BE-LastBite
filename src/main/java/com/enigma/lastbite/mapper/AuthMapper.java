package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.request.AdminRegisterRequest;
import com.enigma.lastbite.dto.request.CustomerRegisterRequest;
import com.enigma.lastbite.dto.request.SellerRegisterRequest;
import com.enigma.lastbite.dto.request.SuperAdminRegisterRequest;
import com.enigma.lastbite.dto.response.RegisterResponse;
import com.enigma.lastbite.entity.Role;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthMapper {

    public static User toUser(CustomerRegisterRequest request, PasswordEncoder passwordEncoder, Set<Role> roles) {
        return User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .profileImageUrl(request.getProfileImageUrl())
                .roles(roles)
                .build();
    }

    public static User toUser(AdminRegisterRequest request, PasswordEncoder passwordEncoder, Set<Role> roles) {
        return User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(request.getProfileImageUrl())
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO)
                .roles(roles)
                .build();
    }

    public static User toUser(SellerRegisterRequest request, PasswordEncoder passwordEncoder, Set<Role> roles) {
        return User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(request.getProfileImageUrl())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .roles(roles)
                .build();
    }

    public static SellerProfile toSellerProfile(SellerRegisterRequest request, User user) {
        return SellerProfile.builder()
                .user(user)
                .storeName(request.getStoreName())
                .storeDescription(request.getStoreDescription())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .balance(BigDecimal.ZERO)
                .averageRatingMenu(BigDecimal.ZERO)
                .build();
    }
    public static User toUser(SuperAdminRegisterRequest request, PasswordEncoder passwordEncoder, Set<Role> roles) {
        return User.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .profileImageUrl(request.getProfileImageUrl())
                .latitude(BigDecimal.ZERO)
                .longitude(BigDecimal.ZERO)
                .roles(roles)
                .build();
    }

    public static RegisterResponse toRegisterResponse(User user) {
        String roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(", "));

        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(roles)
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}