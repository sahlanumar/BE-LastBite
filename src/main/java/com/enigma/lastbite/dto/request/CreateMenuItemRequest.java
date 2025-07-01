package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.ListingStatus;
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
public class CreateMenuItemRequest {
    private String sellerProfileId;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private Integer quantityAvailable;
    private LocalDateTime displayStartTime;
    private LocalDateTime displayEndTime;
    private ListingStatus status;
}