package com.enigma.lastbite.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCustomerRequest {

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

    @NotBlank(message = "Nomor telepon tidak boleh kosong")
    @Size(min = 10, max = 20, message = "Nomor telepon harus antara 10 dan 20 digit")
    private String phoneNumber;
}
