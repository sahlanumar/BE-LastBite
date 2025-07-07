package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRegisterRequest {

    // Username dibutuhkan untuk login dan tidak boleh duplikat
    @NotBlank(message = "Username tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Size(min = 3, max = 100, message = "Username harus antara 3 dan 100 karakter", groups = ValidationGroups.Create.class)
    private String username;

    // Nama lengkap dibutuhkan untuk identifikasi pengguna
    @NotBlank(message = "Nama lengkap tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Size(max = 255, message = "Nama lengkap maksimal 255 karakter", groups = ValidationGroups.Create.class)
    private String fullName;

    // Email dibutuhkan untuk verifikasi dan komunikasi, harus unik dan formatnya valid
    @NotBlank(message = "Email tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Email(message = "Format email tidak valid", groups = ValidationGroups.Create.class)
    private String email;

    // Password dibutuhkan untuk keamanan akun
    @NotBlank(message = "Password tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Size(min = 8, message = "Password minimal 8 karakter", groups = ValidationGroups.Create.class)
    private String password;

    // Nomor telepon dibutuhkan untuk kontak dan verifikasi
    @NotBlank(message = "Nomor telepon tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Size(min = 10, max = 20, message = "Nomor telepon harus antara 10 dan 20 digit", groups = ValidationGroups.Create.class)
    private String phoneNumber;

    // Latitude dibutuhkan untuk menentukan lokasi customer
    @NotNull(message = "Latitude tidak boleh kosong", groups = ValidationGroups.Create.class)
    private BigDecimal latitude;

    // Longitude dibutuhkan untuk menentukan lokasi customer
    @NotNull(message = "Longitude tidak boleh kosong", groups = ValidationGroups.Create.class)
    private BigDecimal longitude;

    private String profileImageUrl;
}