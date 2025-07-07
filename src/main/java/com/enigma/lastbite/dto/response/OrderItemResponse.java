package com.enigma.lastbite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {
    private String menuItemName;
    private Integer quantity;
    private BigDecimal pricePerItem;
    private String productImageUrl;
}
