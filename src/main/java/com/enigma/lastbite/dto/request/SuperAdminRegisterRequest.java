package com.enigma.lastbite.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuperAdminRegisterRequest {

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 3, max = 100, message = "Username harus antara 3 dan 100 karakter")
    private String username;

    @NotBlank(message = "Nama lengkap tidak boleh kosong")
    @Size(max = 255, message = "Nama lengkap maksimal 255 karakter")
    private String fullName;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, message = "Password minimal 8 karakter")
    private String password;

    private String phoneNumber;

    private String profileImageUrl;

    @NotBlank(message = "Kunci rahasia super admin tidak boleh kosong")
    private String secretKeySuperAdmin;
}