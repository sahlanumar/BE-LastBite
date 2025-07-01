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
}
