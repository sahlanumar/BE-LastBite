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

    public OrderServiceImpl(
            OrderRepository orderRepository,
            MenuItemService menuItemService,
            UserService userService,
            JwtUtils jwtUtils,
            @Lazy PaymentService paymentService,SimpMessagingTemplate messagingTemplate,
            @Lazy CartService cartService
    ) {
        this.orderRepository = orderRepository;
        this.menuItemService = menuItemService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.jwtUtils = jwtUtils;
        this.messagingTemplate = messagingTemplate;
        this.cartService = cartService;
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

        Order savedOrder = orderRepository.saveAndFlush(order);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .build();
        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        return OrderMapper.toResponse(savedOrder, paymentResponse);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public OrderResponse createOrderFromCart(CreateOrderFromCartRequest request) {
        // 1. Dapatkan Customer dari token JWT
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User customer = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. Dapatkan keranjang milik customer (gunakan JOIN FETCH untuk efisiensi)
        Cart cart = cartService.findByCustomerId(customer.getId());

        // 3. Filter item di keranjang berdasarkan sellerId dari request
        List<CartItem> itemsForOrder = cart.getItems().stream()
                .filter(cartItem -> cartItem.getMenuItem().getSellerProfile().getId().equals(request.getSellerId()))
                .collect(Collectors.toList());

        // 4. Jika tidak ada item untuk seller tersebut, throw error
        if (itemsForOrder.isEmpty()) {
            throw new CustomException(ErrorCode.CART_EMPTY_FOR_SELLER); // Anda perlu menambahkan ErrorCode ini
        }

        // 5. Inisialisasi variabel-variabel untuk order
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        // Dapatkan seller profile dari item pertama (karena semuanya dari seller yang sama)
        SellerProfile sellerProfile = itemsForOrder.get(0).getMenuItem().getSellerProfile();

        // 6. Loop melalui item yang sudah difilter untuk membuat OrderItem
        for (CartItem cartItem : itemsForOrder) {
            MenuItem menuItem = cartItem.getMenuItem();
            Integer quantity = cartItem.getQuantity();

            // Validasi stok dan ketersediaan (sama seperti metode lama Anda)
            if (menuItem.getQuantityAvailable() < quantity) {
                throw new CustomException(ErrorCode.OUT_OF_STOCK);
            }
            if (menuItem.getDisplayEndTime().isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorCode.MENU_ITEM_NOT_AVAILABLE);
            }

            // Buat OrderItem dari CartItem (bisa dibuatkan mapper khusus)
            OrderItem orderItem = OrderMapper.toOrderItemEntity(cartItem);
            orderItems.add(orderItem);

            // Kurangi stok
            menuItem.setQuantityAvailable(menuItem.getQuantityAvailable() - quantity);
            menuItemService.save(menuItem); // atau simpan nanti secara batch

            // Akumulasi total harga
            totalAmount = totalAmount.add(
                    menuItem.getDiscountedPrice().multiply(BigDecimal.valueOf(quantity))
            );
        }

        // 7. Buat entitas Order
        String verificationCode = generateVerificationCode();
        Order order = OrderMapper.toOrderEntity(customer, sellerProfile, orderItems, totalAmount, verificationCode);

        Order savedOrder = orderRepository.saveAndFlush(order);

        // 8. HAPUS item yang sudah di-checkout dari keranjang
        cart.getItems().removeAll(itemsForOrder);
        cartService.save(cart);

        // 9. Buat pembayaran
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(savedOrder.getId())
                .build();
        PaymentResponse paymentResponse = paymentService.createPayment(paymentRequest);

        // 10. Kembalikan response
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

        String destination = String.format("/topic/order/%s", orderId);
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

        String destination = String.format("/topic/order/%s", orderId);
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
        long sellerCount = userService.countByRole(UserRole.ROLE_SELLER);

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