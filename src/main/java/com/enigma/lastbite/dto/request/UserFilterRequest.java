package com.enigma.lastbite.dto.request;


import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.constant.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFilterRequest {
    private UserRole role;           // ROLE_CUSTOMER, ROLE_ADMIN, …
    private UserStatus status;       // ACTIVE, SUSPENDED, …
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private String search;           // bebas: username / fullName / email
}

