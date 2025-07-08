package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.UsernameOrEmailRequired;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk request login.
 * Menerapkan validasi custom di level kelas untuk memastikan
 * pengguna mengisi 'username' atau 'email'.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@UsernameOrEmailRequired
@Builder
public class LoginRequest {

    /**
     * Username pengguna. Opsional, bisa digantikan dengan email.
     */
    private String username;

    /**
     * Email pengguna. Opsional, bisa digantikan dengan username.
     */
    private String email;

    /**
     * Password pengguna. Field ini wajib diisi.
     */
    @NotBlank(message = "Password tidak boleh kosong")
    private String password;
}