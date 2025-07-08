package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.dto.request.MenuItemReviewCreateRequest;
import com.enigma.lastbite.dto.response.MenuItemReviewResponse;
import com.enigma.lastbite.dto.response.UnreviewedItemResponse;
import com.enigma.lastbite.entity.*;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.MenuMapper;
import com.enigma.lastbite.mapper.MenuReviewMapper;
import com.enigma.lastbite.repository.MenuItemReviewRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MenuItemReviewServiceImpl implements MenuItemReviewService {

    private final MenuItemReviewRepository menuItemReviewRepository;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    @Lazy
    private final SellerService sellerService;
    private final MenuItemService menuItemService;
    private final OrderService orderService;

    @Override
    public MenuItemReviewResponse createReview(MenuItemReviewCreateRequest request) {
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Order order = orderService.findOrderByIdOrThrow(request.getOrderId());

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REVIEW);
        }

        if (!order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            throw new CustomException(ErrorCode.ORDER_NOT_COMPLETED);
        }

        MenuItem menuItem = menuItemService.findById(request.getMenuItemId());

        boolean isItemInOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getMenuItem().getId().equals(request.getMenuItemId()));
        if (!isItemInOrder) {
            throw new CustomException(ErrorCode.MENU_ITEM_NOT_IN_ORDER);
        }

        if (menuItemReviewRepository.existsByOrderIdAndMenuItemId(request.getOrderId(), request.getMenuItemId())) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

        MenuItemReview review = MenuReviewMapper.toMenuItemReviewEntity(request, order, menuItem);
        menuItemReviewRepository.save(review);

        updateMenuItemAverageRating(menuItem.getId());

        return MenuReviewMapper.toMenuItemReviewResponse(review);
    }

    @Override
    public List<UnreviewedItemResponse> getUnreviewedItemsForCustomer() {
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Order> completedOrders = orderService.findAllCompletedOrdersByCustomerId(customer.getId());

        List<UnreviewedItemResponse> unreviewedItems = new ArrayList<>();

        for (Order order : completedOrders) {
            Set<String> reviewedMenuItemIds = menuItemReviewRepository.findAllByOrderId(order.getId())
                    .stream()
                    .map(review -> review.getMenuItem().getId())
                    .collect(Collectors.toSet());

            order.getOrderItems().stream()
                    .filter(orderItem -> !reviewedMenuItemIds.contains(orderItem.getMenuItem().getId()))
                    .forEach(unreviewedOrderItem -> {
                        UnreviewedItemResponse response = UnreviewedItemResponse.builder()
                                .orderId(order.getId())
                                .menuItem(MenuMapper.toMenuItemResponse(unreviewedOrderItem.getMenuItem()))
                                .build();
                        unreviewedItems.add(response);
                    });
        }

        return unreviewedItems;
    }


    @Override
    public void deleteReview(String reviewId) {
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        MenuItemReview review = menuItemReviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getOrder().getCustomer().getId().equals(customer.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

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
                .collect(Collectors.toList());
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

        SellerProfile sellerProfile = sellerService.findBySellerId(menuItem.getSellerProfile().getId());
        sellerProfile.setAverageRatingMenu(BigDecimal.valueOf(menuItemService.averageRatingBySellerProfileId(menuItem.getSellerProfile().getId())));
        sellerService.save(sellerProfile);
    }
}

