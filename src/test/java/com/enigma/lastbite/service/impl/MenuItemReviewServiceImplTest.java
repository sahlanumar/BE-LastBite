
package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.dto.request.MenuItemReviewCreateRequest;
import com.enigma.lastbite.dto.response.MenuItemReviewResponse;
import com.enigma.lastbite.entity.*;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.repository.MenuItemReviewRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.service.OrderService;
import com.enigma.lastbite.service.SellerService;
import com.enigma.lastbite.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemReviewServiceImplTest {

    @Mock
    private MenuItemReviewRepository menuItemReviewRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @Mock
    private SellerService sellerService;

    @Mock
    private MenuItemService menuItemService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private MenuItemReviewServiceImpl menuItemReviewService;

    private User user;
    private Order order;
    private MenuItem menuItem;
    private MenuItemReviewCreateRequest reviewCreateRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("userId");
        user.setUsername("testuser");

        order = new Order();
        order.setId("orderId");
        order.setCustomer(user);
        order.setOrderStatus(OrderStatus.COMPLETED);

        menuItem = new MenuItem();
        menuItem.setId("menuItemId");
        menuItem.setSellerProfile(new SellerProfile());

        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(menuItem);
        order.setOrderItems(Collections.singletonList(orderItem));

        reviewCreateRequest = new MenuItemReviewCreateRequest();
        reviewCreateRequest.setOrderId("orderId");
        reviewCreateRequest.setMenuItemId("menuItemId");
        reviewCreateRequest.setRating(5);
        reviewCreateRequest.setComment("Great food!");
    }

    @Test
    void createReview_Success() {
        when(jwtUtils.getTokenFromHeader()).thenReturn("token");
        when(jwtUtils.getUsernameFromJwtToken("token")).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderService.findOrderByIdOrThrow("orderId")).thenReturn(order);
        when(menuItemService.findById("menuItemId")).thenReturn(menuItem);
        when(menuItemReviewRepository.existsByOrderIdAndMenuItemId("orderId", "menuItemId")).thenReturn(false);
        when(menuItemReviewRepository.save(any(MenuItemReview.class))).thenAnswer(i -> i.getArguments()[0]);

        MenuItemReviewResponse response = menuItemReviewService.createReview(reviewCreateRequest);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Great food!", response.getComment());
    }

    @Test
    void createReview_DuplicateReview() {
        when(jwtUtils.getTokenFromHeader()).thenReturn("token");
        when(jwtUtils.getUsernameFromJwtToken("token")).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(orderService.findOrderByIdOrThrow("orderId")).thenReturn(order);
        when(menuItemService.findById("menuItemId")).thenReturn(menuItem);
        when(menuItemReviewRepository.existsByOrderIdAndMenuItemId("orderId", "menuItemId")).thenReturn(true);

        assertThrows(CustomException.class, () -> menuItemReviewService.createReview(reviewCreateRequest));
    }
}
