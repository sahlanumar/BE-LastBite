package com.enigma.lastbite.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CartGroupedResponse {
    private String cartId;
    private String customerId;
    private List<SellerCartResponse> sellers;
    private BigDecimal grandTotal;
    private LocalDateTime updatedAt;
}