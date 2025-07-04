package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO untuk memperbarui data profil pengguna.
 * Semua field bersifat opsional.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {

    // Nama lengkap baru pengguna. Jika diisi, tidak boleh string kosong.
    @Size(min = 1, max = 255, message = "Nama lengkap harus antara 1 dan 255 karakter", groups = ValidationGroups.Update.class)
    private String fullName;

    // Email baru pengguna. Jika diisi, harus memiliki format email yang valid.
    @Email(message = "Format email tidak valid", groups = ValidationGroups.Update.class)
    private String email;

    // Nomor telepon baru pengguna. Jika diisi, harus memiliki panjang yang valid.
    @Size(min = 10, max = 20, message = "Nomor telepon harus antara 10 dan 20 digit", groups = ValidationGroups.Update.class)
    private String phoneNumber;

    // Koordinat latitude baru. Opsional.
    private BigDecimal latitude;

    // Koordinat longitude baru. Opsional.
    private BigDecimal longitude;

    private LocalDateTime suspendedUntil;

}