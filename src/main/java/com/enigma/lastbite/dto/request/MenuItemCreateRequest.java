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
import org.hibernate.validator.constraints.URL; // Pastikan import ini ada

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ValidPriceDiscount(groups = ValidationGroups.Create.class)
public class MenuItemCreateRequest {

    @NotBlank(message = "Seller Profile ID tidak boleh kosong", groups = ValidationGroups.Create.class)
    private String sellerProfileId;

    @NotBlank(message = "Nama tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Size(max = 100, message = "Nama maksimal 100 karakter", groups = ValidationGroups.Create.class)
    private String name;

    @Size(max = 500, message = "Deskripsi maksimal 500 karakter", groups = ValidationGroups.Create.class)
    private String description;

    /**
     * DIKEMBALIKAN: URL gambar dari item menu.
     * Bersifat opsional, namun jika diisi harus memiliki format URL yang valid.
     */
    @URL(message = "Format URL gambar tidak valid", groups = ValidationGroups.Create.class)
    private String imageUrl;

    @NotNull(message = "Harga asli tidak boleh kosong", groups = ValidationGroups.Create.class)
    @PositiveOrZero(message = "Harga asli harus 0 atau lebih", groups = ValidationGroups.Create.class)
    private BigDecimal originalPrice;

    @PositiveOrZero(message = "Harga diskon harus 0 atau lebih", groups = ValidationGroups.Create.class)
    private BigDecimal discountedPrice;

    @NotNull(message = "Kuantitas tidak boleh kosong", groups = ValidationGroups.Create.class)
    @PositiveOrZero(message = "Kuantitas harus 0 atau lebih", groups = ValidationGroups.Create.class)
    private Integer quantityAvailable;

    private LocalDateTime displayStartTime;
    private LocalDateTime displayEndTime;

    @NotNull(message = "Status tidak boleh kosong", groups = ValidationGroups.Create.class)
    private ListingStatus status;
}