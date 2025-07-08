package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO untuk memperbarui data profil seorang seller.
 * Semua field bersifat opsional, validasi hanya berlaku jika field diisi.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SellerUpdateRequest {

    // Nama baru untuk toko. Jika diisi, tidak boleh string kosong dan maksimal 255 karakter.
    @Size(min = 1, max = 255, message = "Nama toko harus antara 1 dan 255 karakter", groups = ValidationGroups.Update.class)
    private String storeName;

    // Deskripsi baru untuk toko. Jika diisi, maksimal 1000 karakter.
    @Size(max = 1000, message = "Deskripsi toko maksimal 1000 karakter", groups = ValidationGroups.Update.class)
    private String storeDescription;

    // Alamat baru untuk toko. Jika diisi, tidak boleh string kosong.
    @Size(min = 1, message = "Alamat tidak boleh kosong", groups = ValidationGroups.Update.class)
    private String address;

    // Koordinat latitude baru. Opsional.
    private BigDecimal latitude;

    // Koordinat longitude baru. Opsional.
    private BigDecimal longitude;

    // Status baru untuk user/seller (misal: ACTIVE, INACTIVE). Opsional.
    private UserStatus status;

    private String cancelReason;

    private String storeImageUrl;
}