package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.request.MenuItemCreateRequest;
import com.enigma.lastbite.dto.request.MenuItemUpdateRequest;
import com.enigma.lastbite.dto.response.MenuItemResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.SellerProfile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MenuMapper {

    public static MenuItem toMenuItemEntity(MenuItemCreateRequest request, SellerProfile sellerProfile) {
        return MenuItem.builder()
                .sellerProfile(sellerProfile)
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .originalPrice(request.getOriginalPrice())
                .discountedPrice(request.getDiscountedPrice())
                .quantityAvailable(request.getQuantityAvailable())
                .displayStartTime(request.getDisplayStartTime())
                .displayEndTime(request.getDisplayEndTime())
                .status(request.getStatus())
                .averageRating(BigDecimal.valueOf(0.0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())

                .build();
    }

    public static void updateFromDto(MenuItem menuItem, MenuItemUpdateRequest request) {
        if (request.getName() != null) {
            menuItem.setName(request.getName());
        }
        if (request.getDescription() != null) {
            menuItem.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            menuItem.setImageUrl(request.getImageUrl());
        }
        if (request.getOriginalPrice() != null) {
            menuItem.setOriginalPrice(request.getOriginalPrice());
        }
        if (request.getDiscountedPrice() != null) {
            menuItem.setDiscountedPrice(request.getDiscountedPrice());
        }
        if (request.getQuantityAvailable() != null) {
            menuItem.setQuantityAvailable(request.getQuantityAvailable());
        }
        if (request.getDisplayStartTime() != null) {
            menuItem.setDisplayStartTime(request.getDisplayStartTime());
        }
        if (request.getDisplayEndTime() != null) {
            menuItem.setDisplayEndTime(request.getDisplayEndTime());
        }
        if (request.getIsDeleted() != null) {
            menuItem.setDeleted(request.getIsDeleted());
        }
    }

    public static MenuItemResponse toMenuItemResponse(MenuItem menuItem) {
        Double distanceKm = null;
        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .sellerProfileId(menuItem.getSellerProfile().getId())
                .storeName(menuItem.getSellerProfile().getStoreName())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .imageUrl(menuItem.getImageUrl())
                .latitude(menuItem.getSellerProfile().getLatitude())
                .longitude(menuItem.getSellerProfile().getLongitude())
                .storeDescription(menuItem.getSellerProfile().getStoreDescription())
                .originalPrice(menuItem.getOriginalPrice())
                .discountedPrice(menuItem.getDiscountedPrice())
                .quantityAvailable(menuItem.getQuantityAvailable())
                .displayStartTime(menuItem.getDisplayStartTime())
                .displayEndTime(menuItem.getDisplayEndTime())
                .status(menuItem.getStatus())
                .address(menuItem.getSellerProfile().getAddress())
                .isDelleted(menuItem.isDeleted())
                .averageRating(menuItem.getAverageRating())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .build();
    }

    public static MenuItemResponse toMenuItemResponse(MenuItem menuItem, Double userLat, Double userLon) {
        Double distanceKm = null;
        if (userLat != null && userLon != null) {
            distanceKm = haversine(userLat, userLon,
                    menuItem.getSellerProfile().getLatitude().doubleValue(),
                    menuItem.getSellerProfile().getLongitude().doubleValue());
        }

        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .sellerProfileId(menuItem.getSellerProfile().getId())
                .storeName(menuItem.getSellerProfile().getStoreName())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .storeDescription(menuItem.getSellerProfile().getStoreDescription())
                .imageUrl(menuItem.getImageUrl())
                .originalPrice(menuItem.getOriginalPrice())
                .discountedPrice(menuItem.getDiscountedPrice())
                .quantityAvailable(menuItem.getQuantityAvailable())
                .latitude(menuItem.getSellerProfile().getLatitude())
                .longitude(menuItem.getSellerProfile().getLongitude())
                .displayStartTime(menuItem.getDisplayStartTime())
                .displayEndTime(menuItem.getDisplayEndTime())
                .status(menuItem.getStatus())
                .address(menuItem.getSellerProfile().getAddress())
                .isDelleted(menuItem.isDeleted())
                .averageRating(menuItem.getAverageRating())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .distanceKm(distanceKm)
                .build();
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}