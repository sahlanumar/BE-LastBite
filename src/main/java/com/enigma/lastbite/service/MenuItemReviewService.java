package com.enigma.lastbite.service;


import com.enigma.lastbite.dto.request.CreateMenuItemReviewRequest;
import com.enigma.lastbite.dto.response.MenuItemReviewResponse;

import java.util.List;

public interface MenuItemReviewService {

    MenuItemReviewResponse createReview(CreateMenuItemReviewRequest request);

    void deleteReview(String reviewId);

    MenuItemReviewResponse getReviewById(String reviewId);

    List<MenuItemReviewResponse> getReviewsByMenuItemId(String menuItemId);

}