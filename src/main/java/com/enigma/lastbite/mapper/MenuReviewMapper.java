package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.request.MenuItemReviewCreateRequest;
import com.enigma.lastbite.dto.response.MenuItemReviewResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.MenuItemReview;
import com.enigma.lastbite.entity.Order; // <-- Perubahan

public class MenuReviewMapper {

    public static MenuItemReview toMenuItemReviewEntity(MenuItemReviewCreateRequest request, Order order, MenuItem menuItem) {
        return MenuItemReview.builder()
                .order(order)
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
                .profileImageUrl(menuItemReview.getOrder().getCustomer().getProfileImageUrl())
                .createdAt(menuItemReview.getCreatedAt())
                .orderId(menuItemReview.getOrder().getId())
                .menuItemId(menuItemReview.getMenuItem().getId())
                .customerName(menuItemReview.getOrder().getCustomer().getFullName())
                .build();
    }
}