package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.*;
import com.enigma.lastbite.dto.response.OrderResponse;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.dto.response.ReportResponse;
import com.enigma.lastbite.entity.*;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.OrderMapper;
import com.enigma.lastbite.repository.MenuItemReviewRepository;
import com.enigma.lastbite.repository.OrderRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.*;
import com.enigma.lastbite.specification.OrderSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemService menuItemService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PaymentService paymentService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CartService cartService;
    private final SellerService sellerService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            MenuItemService menuItemService,
            UserService userService,
            JwtUtils jwtUtils,
            @Lazy PaymentService paymentService,SimpMessagingTemplate messagingTemplate,
            @Lazy CartService cartService,
            @Lazy SellerService sellerService
    ) {
        this.orderRepository = orderRepository;
        this.menuItemService = menuItemService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.jwtUtils = jwtUtils;
        this.messagingTemplate = messagingTemplate;
        this.cartService = cartService;
        this.sellerService = sellerService;
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

        for (OrderItemRequest itemRequest : request.getOrderItems()) {
            MenuItem menuItem = menuItemService.findById(itemRequest.getMenuItemId());

            if(menuItem.getQuantityAvailable() < itemRequest.getQuantity()) {
                throw new CustomException(ErrorCode.OUT_OF_STOCK);
            }

            if(menuItem.getDisplayEndTime().isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorCode.MENU_ITEM_NOT_AVAILABLE);
            }

            if (sellerProfile == null) {
                sellerProfile = menuItem.getSellerProfile();

            } else if (!Objects.equals(sellerProfile.getId(), menuItem.getSellerProfile().getId())) {
                throw new CustomException(ErrorCode.INVALID_MENU_ITEM);
            }


            OrderItem orderItem = OrderMapper.toOrderItemEntity(itemRequest, menuItem);
            orderItems.add(orderItem);

            menuItem.setQuantityAvailable(menuItem.getQuantityAvailable() - itemRequest.getQuantity());

            menuItemService.save(menuItem);

            totalAmount = totalAmount.add(
                    menuItem.getDiscountedPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()))
            );
        }

        String verificationCode = generateVerificationCode();
        Order order = OrderMapper.toOrderEntity(customer, sellerProfile, orderItems, totalAmount, verificationCode);

        Order savedOrder = orderRepository.save(order);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .build();
        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        savedOrder.setUrlMidtrans(paymentResponse.getRedirectUrl());

        log.info("Order created with id: {}", savedOrder.getId());
        log.info("Redirect URL: {}", paymentResponse.getRedirectUrl());

        orderRepository.save(savedOrder);

        return OrderMapper.toResponse(savedOrder, paymentResponse);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderResponse createOrderFromCart(CreateOrderFromCartRequest request) {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        Cart cart = cartService.findByCustomerId(customer.getId());


        List<CartItem> allItemsForSeller = cart.getItems().stream()
                .filter(cartItem -> cartItem.getMenuItem().getSellerProfile().getId().equals(request.getSellerId()))
                .collect(Collectors.toList());

        if (allItemsForSeller.isEmpty()) {
            throw new CustomException(ErrorCode.CART_EMPTY_FOR_SELLER);
        }

        LocalDateTime now = LocalDateTime.now();
        List<CartItem> availableItemsForOrder = allItemsForSeller.stream()
                .filter(cartItem -> {
                    MenuItem menuItem = cartItem.getMenuItem();
                    boolean isStockAvailable = menuItem.getQuantityAvailable() >= cartItem.getQuantity();
                    boolean isTimeAvailable = !now.isAfter(menuItem.getDisplayEndTime()) && !now.isBefore(menuItem.getDisplayStartTime());
                    return isStockAvailable && isTimeAvailable;
                })
                .collect(Collectors.toList());

        if (availableItemsForOrder.isEmpty()) {
            throw new CustomException(ErrorCode.NO_AVAILABLE_ITEMS_FOR_CHECKOUT);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        SellerProfile sellerProfile = availableItemsForOrder.get(0).getMenuItem().getSellerProfile();

        for (CartItem cartItem : availableItemsForOrder) {
            MenuItem menuItem = cartItem.getMenuItem();

            OrderItem orderItem = OrderMapper.toOrderItemEntity(cartItem);
            orderItems.add(orderItem);

            menuItem.setQuantityAvailable(menuItem.getQuantityAvailable() - cartItem.getQuantity());
            menuItemService.save(menuItem);

            totalAmount = totalAmount.add(
                    menuItem.getDiscountedPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        String verificationCode = generateVerificationCode();
        Order order = OrderMapper.toOrderEntity(customer, sellerProfile, orderItems, totalAmount, verificationCode);

        Order savedOrder = orderRepository.saveAndFlush(order);

        cart.getItems().removeAll(availableItemsForOrder);
        cartService.save(cart);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .build();
        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        savedOrder.setUrlMidtrans(paymentResponse.getRedirectUrl());

        orderRepository.save(savedOrder);

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
        System.out.println("masuk sini");
        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        String sellerId = updatedOrder.getSellerProfile().getId();
        String destination = String.format("/topic/seller/%s", sellerId);
        log.info("WebSocket: Sent PAID notification for order {}", orderId);

        OrderResponse response = OrderMapper.toResponse(updatedOrder);
        messagingTemplate.convertAndSend(destination, response);
    }

    @Override
    public void updateStatusToCancelled(String orderId) {
        Order order = findOrderByIdOrThrow(orderId);
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new CustomException(ErrorCode.ORDER_NOT_PENDING_PAYMENT);
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        System.out.println("masuk sini");
        order.setUpdatedAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.save(order);

        String sellerId = updatedOrder.getSellerProfile().getId();
        String destination = String.format("/topic/seller/%s", sellerId);
        log.info("WebSocket: Sent PAID notification for order {}", orderId);

        OrderResponse response = OrderMapper.toResponse(updatedOrder);
        messagingTemplate.convertAndSend(destination, response);
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
        Order updatedOrder = orderRepository.save(order);
        OrderResponse response = OrderMapper.toResponse(updatedOrder);

        String destination = String.format("/topic/customer/%s", order.getCustomer().getId());
        messagingTemplate.convertAndSend(destination, response);

        return response;
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
        Order updatedOrder = orderRepository.save(order);
        OrderResponse response = OrderMapper.toResponse(updatedOrder);

        String destination = String.format("/topic/customer/%s", order.getCustomer().getId());
        messagingTemplate.convertAndSend(destination, response);
        log.info("WebSocket: Sent READY_FOR_PICKUP notification for order {}", orderId);

        return response;
    }

    @Override
    public OrderResponse cancelOrder(String orderId) {
        Order order = findOrderByIdOrThrow(orderId);

        for (OrderItem orderItem : order.getOrderItems()) {
            MenuItem menuItem = menuItemService.findById(orderItem.getMenuItem().getId());
            menuItem.setQuantityAvailable(menuItem.getQuantityAvailable() + orderItem.getQuantityPurchased());
            menuItemService.save(menuItem);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponse> getAllOrders(OrderFilterRequest filter, int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by("asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortField == null || sortField.isBlank() ? "createdAt" : sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orders = orderRepository.findAll(OrderSpecification.build(filter), pageable);

        return orders.map(OrderMapper::toResponse);
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
        BigDecimal totalAmount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.9));
        sellerProfile.setBalance(sellerProfile.getBalance().add(totalAmount));
        String destination = String.format("/topic/customer/%s", order.getCustomer().getId());
        messagingTemplate.convertAndSend(destination, OrderMapper.toResponse(order));
        log.info("WebSocket: Sent COMPLETED notification for order {}", orderId);

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        return OrderMapper.toResponse(findOrderByIdOrThrow(orderId));
    }

    @Override
    public Page<OrderResponse> getAllOrdersForCustomer(OrderFilterRequest filter,
                                                       int page, int size,
                                                       String sortField, String sortDir) {

        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        filter.setCustomerId(customer.getId().toString());

        Sort sort = Sort.by("asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                (sortField == null || sortField.isBlank()) ? "createdAt"
                        : sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders = orderRepository.findAll(OrderSpecification.build(filter), pageable);
        return orders.map(OrderMapper::toResponse);
    }

    @Override
    public Page<OrderResponse> getAllOrdersForSeller(OrderFilterRequest filter,
                                                     int page, int size,
                                                     String sortField, String sortDir) {

        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User sellerUser = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        SellerProfile sellerProfile = sellerService.findBySellerId(sellerUser.getId());

        filter.setSellerId(sellerProfile.getId().toString());

        Sort sort = Sort.by("asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC
                        : Sort.Direction.DESC,
                (sortField == null || sortField.isBlank()) ? "createdAt"
                        : sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orders = orderRepository.findAll(OrderSpecification.build(filter), pageable);
        return orders.map(OrderMapper::toResponse);
    }


    @Override
    public List<Order> findAllCompletedOrdersByCustomerId(String customerId) {
        return orderRepository.findAllByCustomer_IdAndOrderStatus(customerId, OrderStatus.COMPLETED);
    }


    @Override
    public List<Order> findListOrderByCustomerIdAndMenuItemId(String customerId, String menuItemId) {
        return orderRepository.findCompletedOrdersByCustomerAndMenuItem(customerId, menuItemId);
    }



    @Override
    public Order findOrderByIdOrThrow(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Override
    public ReportResponse getReport(ReportFilterRequest filter) {
        long customerCount = userService.countByRole(UserRole.ROLE_CUSTOMER);
        long sellerCount = sellerService.countByStatus(UserStatus.ACTIVE);

        LocalDateTime start = filter.getStart();
        LocalDateTime end = filter.getEnd();

        long successTx;
        BigDecimal successAmount;

        if (start != null && end != null) {
            successTx = orderRepository.countByStatusBetween(OrderStatus.COMPLETED, start, end);
            successAmount = orderRepository.sumTotalAmountByStatusBetween(OrderStatus.COMPLETED, start, end);
        } else if (start != null) {
            successTx = orderRepository.countByStatusAndStart(OrderStatus.COMPLETED, start);
            successAmount = orderRepository.sumTotalAmountByStatusAndStart(OrderStatus.COMPLETED, start);
        } else if (end != null) {
            successTx = orderRepository.countByStatusAndEnd(OrderStatus.COMPLETED, end);
            successAmount = orderRepository.sumTotalAmountByStatusAndEnd(OrderStatus.COMPLETED, end);
        } else {
            successTx = orderRepository.countByStatus(OrderStatus.COMPLETED);
            successAmount = orderRepository.sumTotalAmountByStatus(OrderStatus.COMPLETED);
        }

        return OrderMapper.toResponse(customerCount, sellerCount, successTx, successAmount);
    }

    @Override
    public long countCompletedOrdersBySellerId(String sellerId) {
        return orderRepository.countCompletedOrdersBySellerProfileId(sellerId);
    }

}