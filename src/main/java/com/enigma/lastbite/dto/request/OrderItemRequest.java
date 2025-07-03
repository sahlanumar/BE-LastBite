package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO untuk merepresentasikan satu item dalam sebuah pesanan.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {
    /**
     * ID dari item menu yang dipesan. Wajib diisi.
     */
    @NotBlank(message = "Menu Item ID tidak boleh kosong", groups = ValidationGroups.Create.class)
    private String menuItemId;

    /**
     * Jumlah kuantitas dari item menu yang dipesan. Wajib diisi dan minimal 1.
     */
    @NotNull(message = "Kuantitas tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Min(value = 1, message = "Kuantitas minimal harus 1", groups = ValidationGroups.Create.class)
    private Integer quantity;
}