package com.enigma.lastbite.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class SellerCartResponse {
    private String sellerId;
    private String storeName;
    private String storeImageUrl;
    private List<CartItemResponse> items; // Bisa menggunakan CartItemResponse yang sudah ada
    private BigDecimal sellerSubtotal;
}