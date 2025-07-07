
package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.dto.request.CreateOrderFromCartRequest;
import com.enigma.lastbite.dto.request.PaymentRequest;
import com.enigma.lastbite.dto.response.OrderResponse;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.entity.*;
import com.enigma.lastbite.repository.OrderRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.CartService;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.service.PaymentService;
import com.enigma.lastbite.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuItemService menuItemService;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PaymentService paymentService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Cart cart;
    private MenuItem menuItem;
    private CreateOrderFromCartRequest createOrderFromCartRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("userId");
        user.setUsername("testuser");

        menuItem = new MenuItem();
        menuItem.setId("menuItemId");
        menuItem.setOriginalPrice(BigDecimal.valueOf(10000));
        menuItem.setDiscountedPrice(BigDecimal.valueOf(10000));
        menuItem.setQuantityAvailable(10);
        menuItem.setDisplayStartTime(LocalDateTime.now().minusHours(1));
        menuItem.setDisplayEndTime(LocalDateTime.now().plusHours(1));
        menuItem.setSellerProfile(new SellerProfile());
        menuItem.getSellerProfile().setId("sellerId");

        CartItem cartItem = new CartItem();
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(2);

        cart = new Cart();
        cart.setId("cartId");
        cart.setCustomer(user);
        cart.setItems(new ArrayList<>(Collections.singletonList(cartItem)));

        createOrderFromCartRequest = new CreateOrderFromCartRequest();
        createOrderFromCartRequest.setSellerId("sellerId");
    }

    @Test
    void createOrderFromCart_Success() {
        // 1. Mock untuk mendapatkan user dari token (sudah benar)
        when(jwtUtils.getTokenFromHeader()).thenReturn("token");
        when(jwtUtils.getUsernameFromJwtToken("token")).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        // 2. Mock untuk mendapatkan keranjang customer (sudah benar)
        when(cartService.findByCustomerId("userId")).thenReturn(cart);

        // 3. Mock untuk proses penyimpanan Order (sudah benar)
        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        // 4. [FIX] BARU: Mock untuk panggilan ke PaymentService
        // Buat objek PaymentResponse palsu untuk dikembalikan
        PaymentResponse dummyPaymentResponse = new PaymentResponse();
        dummyPaymentResponse.setRedirectUrl("http://example.com/payment");
        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(dummyPaymentResponse);

        // 5. [FIX] BARU: Mock untuk panggilan saat menyimpan kembali keranjang
        when(cartService.save(any(Cart.class))).thenReturn(cart);


        // Panggil metode yang sedang diuji
        OrderResponse response = orderService.createOrderFromCart(createOrderFromCartRequest);

        // Assertions (sudah benar)
        assertNotNull(response);
        assertNotNull(response.getPayment()); // Bisa ditambahkan assertion baru
        assertEquals(OrderStatus.PENDING_PAYMENT, response.getStatus());
    }

    @Test
    void acceptOrder_Success() {
        Order order = new Order();
        order.setId("orderId");
        order.setOrderStatus(OrderStatus.PAID);

        when(orderRepository.findById("orderId")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = orderService.acceptOrder("orderId");

        assertNotNull(response);
        assertEquals(OrderStatus.PREPARING, response.getStatus());
    }
}
