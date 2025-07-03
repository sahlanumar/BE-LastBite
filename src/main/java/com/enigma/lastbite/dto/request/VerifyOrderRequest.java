package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk verifikasi penyelesaian pesanan.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOrderRequest {
    /**
     * Kode unik untuk verifikasi pesanan.
     * Wajib diisi, harus 6 karakter, dan hanya berisi huruf kapital dan angka.
     */
    @NotBlank(message = "Kode verifikasi tidak boleh kosong", groups = ValidationGroups.Update.class)
    @Size(min = 6, max = 6, message = "Kode verifikasi harus 6 karakter", groups = ValidationGroups.Update.class)
    @Pattern(regexp = "^[A-Z0-9]{6}$", message = "Kode verifikasi hanya boleh berisi huruf kapital dan angka", groups = ValidationGroups.Update.class)
    private String verificationCode;
}