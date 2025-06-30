package com.enigma.lastbite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private String id; // UUID dari user yang baru dibuat
    private String username;
    private String fullName;
    private String email;
    private String role; // 'customer', 'seller', atau 'admin'
}
