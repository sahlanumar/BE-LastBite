package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.response.MenuItemReviewResponse;
import com.enigma.lastbite.entity.MenuItemReview;

public class MenuReviewMapper {
    public static MenuItemReviewResponse toMenuItemReviewResponse(MenuItemReview menuItemReview) {
        return MenuItemReviewResponse.builder()
                .id(menuItemReview.getId())
                .rating(menuItemReview.getRating())
                .comment(menuItemReview.getComment())
                .createdAt(menuItemReview.getCreatedAt())
                .build();
    }
}
