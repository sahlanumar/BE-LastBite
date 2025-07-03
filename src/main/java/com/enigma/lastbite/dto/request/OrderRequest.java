package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO untuk membuat pesanan baru.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    /**
     * Daftar item yang dipesan.
     * Sebuah pesanan harus memiliki setidaknya satu item.
     * Anotasi @Valid sangat penting di sini untuk memicu validasi
     * pada setiap objek OrderItemRequest di dalam list.
     */
    @NotEmpty(message = "Pesanan harus memiliki setidaknya satu item", groups = ValidationGroups.Create.class)
    @Valid // Memicu validasi pada objek di dalam list
    private List<OrderItemRequest> orderItems;
}