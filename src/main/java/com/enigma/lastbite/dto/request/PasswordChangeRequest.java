package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.PasswordsMatch;
import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO untuk request perubahan password.
 */
@Data
// Terapkan validasi custom di level kelas
@PasswordsMatch(groups = ValidationGroups.Update.class)
public class PasswordChangeRequest {

    // Password lama wajib diisi untuk verifikasi.
    @NotBlank(message = "Password lama tidak boleh kosong", groups = ValidationGroups.Update.class)
    private String oldPassword;

    // Password baru wajib diisi dan memiliki panjang minimal 8 karakter.
    @NotBlank(message = "Password baru tidak boleh kosong", groups = ValidationGroups.Update.class)
    @Size(min = 8, message = "Password baru minimal 8 karakter", groups = ValidationGroups.Update.class)
    private String newPassword;

    // Konfirmasi password baru wajib diisi untuk menghindari salah ketik.
    @NotBlank(message = "Konfirmasi password baru tidak boleh kosong", groups = ValidationGroups.Update.class)
    private String confirmNewPassword;
}