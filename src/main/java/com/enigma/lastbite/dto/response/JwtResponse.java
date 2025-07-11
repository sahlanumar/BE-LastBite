package com.enigma.lastbite.dto.response;

import com.enigma.lastbite.constant.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;

    @Builder.Default
    private String type = "Bearer";

    private String username;
    private String email;
    private List<String> roles;
    private String fullName;
    private UserStatus status;
    private String messageSuspend;
}
