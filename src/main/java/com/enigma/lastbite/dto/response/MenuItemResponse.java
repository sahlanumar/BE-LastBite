package com.enigma.lastbite.dto.response;

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
public class MenuItemResponse {
    private String id;
    private String sellerProfileId;
    private String storeName;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private Integer quantityAvailable;
    private LocalDateTime displayStartTime;
    private LocalDateTime displayEndTime;
    private ListingStatus status;
    private String storeDescription;
    private Boolean isDelleted;
    private BigDecimal averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double distanceKm;
}