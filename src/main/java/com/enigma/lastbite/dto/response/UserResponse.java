package com.enigma.lastbite.dto.response;

import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.constant.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO untuk response yang berisi data detail user.
 * Menyembunyikan informasi sensitif seperti password.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private String id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String role;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String profileImageUrl;
    private String suspendedReason;
    private LocalDateTime suspendedUntil;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
