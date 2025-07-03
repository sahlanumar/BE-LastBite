package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk membuat review atau ulasan baru untuk sebuah item menu.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuItemReviewCreateRequest {

    /**
     * ID dari item menu yang sedang direview.
     * Wajib diisi untuk menautkan review ini ke item yang benar.
     */
    @NotBlank(message = "Menu Item ID tidak boleh kosong", groups = ValidationGroups.Create.class)
    private String menuItemId;

    /**
     * Rating atau peringkat yang diberikan oleh pengguna.
     * Wajib diisi dan harus bernilai antara 1 (bintang 1) hingga 5 (bintang 5).
     */
    @NotNull(message = "Rating tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Min(value = 1, message = "Rating minimal harus 1", groups = ValidationGroups.Create.class)
    @Max(value = 5, message = "Rating maksimal harus 5", groups = ValidationGroups.Create.class)
    private Integer rating;

    /**
     * Komentar atau ulasan teks dari pengguna.
     * Field ini bersifat opsional.
     */
    private String comment;
}