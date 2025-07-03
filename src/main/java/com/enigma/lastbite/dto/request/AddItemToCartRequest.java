package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddItemToCartRequest {
    /**
     * ID menu item tidak boleh kosong karena ini adalah kunci
     * untuk mengetahui item mana yang akan ditambahkan ke keranjang.
     */
    @NotBlank(message = "Menu item ID tidak boleh kosong", groups = ValidationGroups.Create.class)
    private String menuItemId;

    /**
     * Kuantitas tidak boleh null dan harus minimal 1.
     * Tidak valid untuk menambahkan 0 atau kurang item ke keranjang.
     */
    @NotNull(message = "Kuantitas tidak boleh kosong", groups = ValidationGroups.Create.class)
    @Min(value = 1, message = "Kuantitas minimal harus 1", groups = ValidationGroups.Create.class)
    private Integer quantity;
}