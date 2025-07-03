package com.enigma.lastbite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemUpdateRequest {
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private Integer quantityAvailable;
    private LocalDateTime displayStartTime;
    private LocalDateTime displayEndTime;
}