package com.enigma.lastbite.dto.request;


import com.enigma.lastbite.constant.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    private String fullName;

    private String email;

    private String phoneNumber;

    // Field di bawah ini kemungkinan hanya dapat diubah oleh peran Admin.
    // Logika otorisasi akan ditangani di service layer.
    private UserStatus status;

    private LocalDateTime suspendedUntil;

}
