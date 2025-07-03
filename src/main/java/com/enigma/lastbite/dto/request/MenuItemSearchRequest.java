package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.ListingStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data // Lombok akan membuatkan getter, setter, dll.
public class MenuItemSearchRequest {
    private String name;
    private String sellerId;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private Boolean isAvailable;
    private ListingStatus status;
    private Double minRating; // Field untuk filter rating
    private Double maxRating;
    private Double lat;
    private Double lon;
}