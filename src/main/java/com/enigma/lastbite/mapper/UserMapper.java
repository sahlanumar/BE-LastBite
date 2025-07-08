package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.dto.request.UserUpdateRequest;
import com.enigma.lastbite.dto.response.UserResponse;
import com.enigma.lastbite.entity.User;

public class UserMapper {

    public static void updateFromDto(User user, UserUpdateRequest request) {
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getSuspendedUntil() != null) {
            if (user.getRoles().contains(UserRole.ROLE_ADMIN)) {
                throw new RuntimeException("Admin cannot be suspended");
            }
            user.setSuspendedUntil(request.getSuspendedUntil());
            user.setSuspendReason(request.getSuspendedReason());
        }
        if (request.getLatitude() != null) {
            user.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            user.setLongitude(request.getLongitude());
        }
        if(request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
    }

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .updatedAt(user.getUpdatedAt())
                .phoneNumber(user.getPhoneNumber())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .profileImageUrl(user.getProfileImageUrl())
                .role(String.valueOf(user.getRoles().stream().map(role -> role.getName().name()).toList()))
                .suspendedUntil(user.getSuspendedUntil())
                .suspendedReason(user.getSuspendReason())
                .createdAt(user.getCreatedAt())
                .build();
    }
}