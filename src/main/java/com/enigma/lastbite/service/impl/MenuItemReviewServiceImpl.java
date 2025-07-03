package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.dto.request.CreateMenuItemReviewRequest;
import com.enigma.lastbite.dto.response.MenuItemReviewResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.MenuItemReview;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.MenuReviewMapper;
import com.enigma.lastbite.repository.MenuItemReviewRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.MenuItemReviewService;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuItemReviewServiceImpl implements MenuItemReviewService {

    private final MenuItemReviewRepository menuItemReviewRepository;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final MenuItemService menuItemService;

    @Override
    public MenuItemReviewResponse createReview(CreateMenuItemReviewRequest request) {
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MenuItem menuItem = menuItemService.findById(request.getMenuItemId());

        if (menuItemReviewRepository.existsByCustomerIdAndMenuItemId(user.getId(), menuItem.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new CustomException(ErrorCode.INVALID_RATING);
        }

        MenuItemReview review = MenuReviewMapper.toMenuItemReviewEntity(request, user, menuItem);

        menuItemReviewRepository.save(review);

        updateMenuItemAverageRating(menuItem.getId());

        return MenuReviewMapper.toMenuItemReviewResponse(review);
    }

    @Override
    public void deleteReview(String reviewId) {
        MenuItemReview review = menuItemReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        String menuItemId = review.getMenuItem().getId();
        menuItemReviewRepository.delete(review);

        updateMenuItemAverageRating(menuItemId);
    }

    @Override
    public MenuItemReviewResponse getReviewById(String reviewId) {
        MenuItemReview review = menuItemReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));
        return MenuReviewMapper.toMenuItemReviewResponse(review);
    }

    @Override
    public List<MenuItemReviewResponse> getReviewsByMenuItemId(String menuItemId) {
        return menuItemReviewRepository.findAllByMenuItemId(menuItemId)
                .stream()
                .map(MenuReviewMapper::toMenuItemReviewResponse)
                .toList();
    }

    private void updateMenuItemAverageRating(String menuItemId) {
        List<MenuItemReview> reviews = menuItemReviewRepository.findAllByMenuItemId(menuItemId);

        BigDecimal average = BigDecimal.ZERO;
        if (!reviews.isEmpty()) {
            BigDecimal total = reviews.stream()
                    .map(r -> BigDecimal.valueOf(r.getRating()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            average = total.divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);
        }

        MenuItem menuItem = menuItemService.findById(menuItemId);
        menuItem.setAverageRating(average);
        menuItemService.save(menuItem);
    }
}