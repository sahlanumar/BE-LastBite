package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.dto.request.OrderRequest;
import com.enigma.lastbite.dto.request.PaymentRequest;
import com.enigma.lastbite.dto.request.VerifyOrderRequest;
import com.enigma.lastbite.dto.response.OrderResponse;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.entity.*;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.OrderMapper;
import com.enigma.lastbite.repository.MenuItemRepository;
import com.enigma.lastbite.repository.OrderRepository;
import com.enigma.lastbite.repository.UserRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.service.OrderService;
import com.enigma.lastbite.service.PaymentService;
import com.enigma.lastbite.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemService menuItemService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PaymentService paymentService;


    public OrderServiceImpl(
            OrderRepository orderRepository,
            MenuItemService menuItemService,
            UserService userService,
            JwtUtils jwtUtils,
            @Lazy PaymentService paymentService // ⬅️ Letakkan @Lazy di sini
    ) {
        this.orderRepository = orderRepository;
        this.menuItemService = menuItemService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderResponse createOrder(OrderRequest request) {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        SellerProfile sellerProfile = null;

        for (var itemRequest : request.getOrderItems()) {
            MenuItem menuItem = menuItemService.findById(itemRequest.getMenuItemId());

            if (sellerProfile == null) {
                sellerProfile = menuItem.getSellerProfile();
            } else if (!Objects.equals(sellerProfile.getId(), menuItem.getSellerProfile().getId())) {
                throw new CustomException(ErrorCode.INVALID_MENU_ITEM);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantityPurchased(itemRequest.getQuantity());
            orderItem.setPricePerItem(menuItem.getDiscountedPrice());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(
                    menuItem.getDiscountedPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
            );
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setSellerProfile(sellerProfile);
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        order.setVerificationCode(generateVerificationCode());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderItems.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.saveAndFlush(order);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .build();
        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        return OrderMapper.toResponse(savedOrder, paymentResponse);
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateStatusToPaid(String orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new CustomException(ErrorCode.ORDER_NOT_PENDING_PAYMENT);
        }
        order.setOrderStatus(OrderStatus.PAID);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderResponse acceptOrder(String orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new CustomException(ErrorCode.ORDER_NOT_PAID);
        }
        order.setOrderStatus(OrderStatus.PREPARING);
        order.setUpdatedAt(LocalDateTime.now());
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderResponse markAsReadyForPickup(String orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        if (order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new CustomException(ErrorCode.ORDER_NOT_PREPARING);
        }
        order.setOrderStatus(OrderStatus.READY_FOR_PICKUP);
        order.setUpdatedAt(LocalDateTime.now());
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderResponse verifyAndCompleteOrder(String orderId, VerifyOrderRequest request) {
        Order order = findOrderByIdOrThrow(orderId);

        if (order.getOrderStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new CustomException(ErrorCode.ORDER_NOT_READY_FOR_PICKUP);
        }

        if (!Objects.equals(order.getVerificationCode(), request.getVerificationCode())) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());

        SellerProfile sellerProfile = order.getSellerProfile();
        sellerProfile.setBalance(sellerProfile.getBalance().add(order.getTotalAmount()));


        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        return OrderMapper.toResponse(findOrderByIdOrThrow(orderId));
    }

    @Override
    public Page<OrderResponse> getAllOrdersForCustomer(Pageable pageable) {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Order> orders = orderRepository.findAllByCustomer_Id(customer.getId(), pageable);
        return orders.map(OrderMapper::toResponse);
    }

    @Override
    public Page<OrderResponse> getAllOrdersForSeller(Pageable pageable) {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Order> orders = orderRepository.findAllBySellerProfile_User_Id(user.getId(), pageable);
        return orders.map(OrderMapper::toResponse);
    }

    @Override
    public Order findOrderByIdOrThrow(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
    }
}