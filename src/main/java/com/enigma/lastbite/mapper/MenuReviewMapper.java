package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.request.MenuItemReviewCreateRequest;
import com.enigma.lastbite.dto.response.MenuItemReviewResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.MenuItemReview;
import com.enigma.lastbite.entity.User;

public class MenuReviewMapper {

    public static MenuItemReview toMenuItemReviewEntity(MenuItemReviewCreateRequest request, User user, MenuItem menuItem) {
        return MenuItemReview.builder()
                .customer(user)
                .menuItem(menuItem)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
    }

    public static MenuItemReviewResponse toMenuItemReviewResponse(MenuItemReview menuItemReview) {
        return MenuItemReviewResponse.builder()
                .id(menuItemReview.getId())
                .rating(menuItemReview.getRating())
                .comment(menuItemReview.getComment())
                .createdAt(menuItemReview.getCreatedAt())
                .build();
    }
}