package com.enigma.lastbite.mapper;

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
            user.setSuspendedUntil(request.getSuspendedUntil());
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
                .role(String.valueOf(user.getRoles().stream().map(role -> role.getName().name()).toList()))
                .suspendedUntil(user.getSuspendedUntil())
                .createdAt(user.getCreatedAt())
                .build();
    }
}