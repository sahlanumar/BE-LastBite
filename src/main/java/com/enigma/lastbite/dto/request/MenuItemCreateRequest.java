package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.ListingStatus;
import com.enigma.lastbite.validation.ValidPriceDiscount;
import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) untuk membuat item menu baru.
 * Mengandung semua informasi yang diperlukan dan aturan validasi
 * untuk memastikan data yang masuk konsisten dan benar.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Menerapkan validasi custom di level kelas untuk memastikan harga diskon tidak lebih besar dari harga asli.
// Validasi ini hanya aktif pada grup 'Create'.
@ValidPriceDiscount(groups = ValidationGroups.Create.class)
public class MenuItemCreateRequest {

    /**
     * ID profil penjual (seller) yang memiliki item menu ini.
     * Wajib diisi untuk menautkan item ke penjual yang benar.
     */
    @NotBlank(message = "Seller Profile ID tidak boleh kosong", groups = ValidationGroups.Create.class)
    private String sellerProfileId;

    /**
     * Nama dari item menu, contoh: "Nasi Goreng Spesial".
     * Wajib diisi dan memiliki panjang maksimal 100 karakter.
     */
    @NotBlank(message = "Nama tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Size(max = 100, message = "Nama maksimal 100 karakter", groups = ValidationGroups.Create.class)
    private String name;

    /**
     * Deskripsi singkat mengenai item menu.
     * Bersifat opsional, namun jika diisi tidak boleh lebih dari 500 karakter.
     */
    @Size(max = 500, message = "Deskripsi maksimal 500 karakter", groups = ValidationGroups.Create.class)
    private String description;

    /**
     * URL gambar dari item menu.
     * Bersifat opsional, namun jika diisi harus memiliki format URL yang valid.
     */
    @URL(message = "Format URL gambar tidak valid", groups = ValidationGroups.Create.class)
    private String imageUrl;

    /**
     * Harga normal dari item menu sebelum ada diskon.
     * Wajib diisi dan nilainya harus 0 atau lebih besar (tidak boleh negatif).
     */
    @NotNull(message = "Harga asli tidak boleh kosong", groups = ValidationGroups.Create.class)
    @PositiveOrZero(message = "Harga asli harus 0 atau lebih", groups = ValidationGroups.Create.class)
    private BigDecimal originalPrice;

    /**
     * Harga setelah diskon. Bersifat opsional.
     * Jika diisi, nilainya tidak boleh negatif. Validasi tambahan di level kelas
     * akan memastikan harga ini tidak lebih besar dari harga asli.
     */
    @PositiveOrZero(message = "Harga diskon harus 0 atau lebih", groups = ValidationGroups.Create.class)
    private BigDecimal discountedPrice;

    /**
     * Jumlah stok item menu yang tersedia untuk dijual.
     * Wajib diisi dan nilainya harus 0 atau lebih besar.
     */
    @NotNull(message = "Kuantitas tidak boleh kosong", groups = ValidationGroups.Create.class)
    @PositiveOrZero(message = "Kuantitas harus 0 atau lebih", groups = ValidationGroups.Create.class)
    private Integer quantityAvailable;

    /**
     * Waktu mulai item menu ini akan ditampilkan atau tersedia.
     * Bersifat opsional.
     */
    private LocalDateTime displayStartTime;

    /**
     * Waktu berakhir item menu ini akan ditampilkan atau tersedia.
     * Bersifat opsional.
     */
    private LocalDateTime displayEndTime;

    /**
     * Status awal dari listing item menu (misalnya: DRAFT, PUBLISHED).
     * Wajib diisi saat pembuatan item baru.
     */
    @NotNull(message = "Status tidak boleh kosong", groups = ValidationGroups.Create.class)
    private ListingStatus status;
}