package com.enigma.lastbite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private String cartId;
    private String customerId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private LocalDateTime updatedAt;
}