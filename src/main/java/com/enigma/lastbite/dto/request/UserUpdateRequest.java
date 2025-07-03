package com.enigma.lastbite.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    private String fullName;

    private String email;

    private String phoneNumber;

    private LocalDateTime suspendedUntil;

}
