package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.ValidationGroups;
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
 * DTO (Data Transfer Object) untuk memperbarui data item menu yang sudah ada.
 * Semua field bersifat opsional. Validasi hanya akan dijalankan pada field
 * yang nilainya disertakan dalam request body.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemUpdateRequest {

    /**
     * Nama baru untuk item menu.
     * Jika field ini diisi, nilainya tidak boleh string kosong dan maksimal 100 karakter.
     */
    @Size(min = 1, max = 100, message = "Nama harus antara 1 dan 100 karakter", groups = ValidationGroups.Update.class)
    private String name;

    /**
     * Deskripsi baru untuk item menu.
     * Jika diisi, panjangnya tidak boleh melebihi 500 karakter.
     */
    @Size(max = 500, message = "Deskripsi maksimal 500 karakter", groups = ValidationGroups.Update.class)
    private String description;

    /**
     * URL gambar baru untuk item menu.
     * Jika diisi, harus merupakan format URL yang valid.
     */
    @URL(message = "Format URL gambar tidak valid", groups = ValidationGroups.Update.class)
    private String imageUrl;

    /**
     * Harga asli baru.
     * Jika diisi, nilainya tidak boleh negatif.
     */
    @PositiveOrZero(message = "Harga asli harus 0 atau lebih", groups = ValidationGroups.Update.class)
    private BigDecimal originalPrice;

    /**
     * Harga diskon baru.
     * Jika diisi, nilainya tidak boleh negatif.
     * Catatan: Validasi perbandingan dengan harga asli sebaiknya dilakukan di dalam service layer
     * karena service memiliki akses ke data lama dan data baru secara bersamaan.
     */
    @PositiveOrZero(message = "Harga diskon harus 0 atau lebih", groups = ValidationGroups.Update.class)
    private BigDecimal discountedPrice;

    /**
     * Jumlah stok baru yang tersedia.
     * Jika diisi, nilainya tidak boleh negatif.
     */
    @PositiveOrZero(message = "Kuantitas harus 0 atau lebih", groups = ValidationGroups.Update.class)
    private Integer quantityAvailable;

    /**
     * Waktu mulai tampilan yang baru.
     * Bersifat opsional.
     */
    private LocalDateTime displayStartTime;

    /**
     * Waktu berakhir tampilan yang baru.
     * Bersifat opsional.
     */
    private LocalDateTime displayEndTime;

    private Boolean isDeleted;
}