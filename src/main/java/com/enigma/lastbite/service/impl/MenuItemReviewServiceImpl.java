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
    private final OrderService orderService; // Pastikan OrderService di-inject

    @Override
    public MenuItemReviewResponse createReview(MenuItemReviewCreateRequest request) {
        // 1. Dapatkan user yang sedang login
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. Validasi Order
        Order order = orderService.findOrderByIdOrThrow(request.getOrderId());

        // Validasi 2a: Pastikan order milik user yang login
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REVIEW); // Atau error code yang lebih sesuai
        }

        // Validasi 2b: Pastikan order sudah selesai (COMPLETED)
        if (!order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            throw new CustomException(ErrorCode.ORDER_NOT_COMPLETED);
        }

        // 3. Validasi MenuItem
        MenuItem menuItem = menuItemService.findById(request.getMenuItemId());

        // Validasi 3a: Pastikan menu item ada di dalam order tersebut
        boolean isItemInOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getMenuItem().getId().equals(request.getMenuItemId()));
        if (!isItemInOrder) {
            throw new CustomException(ErrorCode.MENU_ITEM_NOT_IN_ORDER); // Buat ErrorCode baru
        }

        // Validasi 4: Pastikan item ini belum direview untuk order ini
        if (menuItemReviewRepository.existsByOrderIdAndMenuItemId(request.getOrderId(), request.getMenuItemId())) {
            throw new CustomException(ErrorCode.DUPLICATE_REVIEW);
        }

        // 5. Jika semua validasi lolos, buat dan simpan review
        MenuItemReview review = MenuReviewMapper.toMenuItemReviewEntity(request, order, menuItem);
        menuItemReviewRepository.save(review);

        // 6. Update rating rata-rata
        updateMenuItemAverageRating(menuItem.getId());

        return MenuReviewMapper.toMenuItemReviewResponse(review);
    }

    // --- API BARU YANG ANDA MINTA ---
    @Override
    public List<UnreviewedItemResponse> getUnreviewedItemsForCustomer() {
        // 1. Dapatkan user yang login
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. Dapatkan semua order yang sudah COMPLETED milik customer
        //    (Anda perlu menambahkan metode ini di OrderService/Repository)
        List<Order> completedOrders = orderService.findAllCompletedOrdersByCustomerId(customer.getId());

        List<UnreviewedItemResponse> unreviewedItems = new ArrayList<>();

        // 3. Iterasi setiap order yang sudah selesai
        for (Order order : completedOrders) {
            // 4. Dapatkan Set berisi ID semua menu item yang SUDAH direview untuk order ini
            Set<String> reviewedMenuItemIds = menuItemReviewRepository.findAllByOrderId(order.getId())
                    .stream()
                    .map(review -> review.getMenuItem().getId())
                    .collect(Collectors.toSet());

            // 5. Iterasi setiap item di dalam order, lalu filter yang BELUM direview
            order.getOrderItems().stream()
                    .filter(orderItem -> !reviewedMenuItemIds.contains(orderItem.getMenuItem().getId()))
                    .forEach(unreviewedOrderItem -> {
                        UnreviewedItemResponse response = UnreviewedItemResponse.builder()
                                .orderId(order.getId())
                                .menuItem(MenuMapper.toMenuItemResponse(unreviewedOrderItem.getMenuItem())) // Gunakan mapper Anda
                                .build();
                        unreviewedItems.add(response);
                    });
        }

        return unreviewedItems;
    }


    @Override
    public void deleteReview(String reviewId) {
        // Validasi tambahan: Hanya user yang membuat review yang boleh menghapus
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

