package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.ListingStatus;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemSearchRequest {
    private String name;
    private String sellerId;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private Boolean isAvailable;
    private ListingStatus status;
    private Double minRating;
    private Double maxRating;
    private Double lat;
    private Double lon;
}