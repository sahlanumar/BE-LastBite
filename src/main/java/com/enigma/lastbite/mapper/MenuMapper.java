package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.response.MenuItemResponse;
import com.enigma.lastbite.entity.MenuItem;

public class MenuMapper {
    public static MenuItemResponse toMenuItemResponse(MenuItem menuItem) {
        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .sellerProfileId(menuItem.getSellerProfile().getId())
                .storeName(menuItem.getSellerProfile().getStoreName())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .imageUrl(menuItem.getImageUrl())
                .originalPrice(menuItem.getOriginalPrice())
                .discountedPrice(menuItem.getDiscountedPrice())
                .quantityAvailable(menuItem.getQuantityAvailable())
                .displayStartTime(menuItem.getDisplayStartTime())
                .displayEndTime(menuItem.getDisplayEndTime())
                .status(menuItem.getStatus())
                .averageRating(menuItem.getAverageRating())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .build();
    }
    public static MenuItemResponse toMenuItemResponse(MenuItem menuItem, Double userLat, Double userLon) {
        Double distanceKm = null;
        if (userLat != null && userLon != null) {
            distanceKm = Double.valueOf(haversine(userLat, userLon,
                    menuItem.getSellerProfile().getLatitude().doubleValue(),
                    menuItem.getSellerProfile().getLongitude().doubleValue()));
        }

        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .sellerProfileId(menuItem.getSellerProfile().getId())
                .storeName(menuItem.getSellerProfile().getStoreName())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .imageUrl(menuItem.getImageUrl())
                .originalPrice(menuItem.getOriginalPrice())
                .discountedPrice(menuItem.getDiscountedPrice())
                .quantityAvailable(menuItem.getQuantityAvailable())
                .displayStartTime(menuItem.getDisplayStartTime())
                .displayEndTime(menuItem.getDisplayEndTime())
                .status(menuItem.getStatus())
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
