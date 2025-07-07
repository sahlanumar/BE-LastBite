package com.enigma.lastbite.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderFromCartRequest {

    @NotBlank(message = "Seller ID tidak boleh kosong")
    private String sellerId;
}