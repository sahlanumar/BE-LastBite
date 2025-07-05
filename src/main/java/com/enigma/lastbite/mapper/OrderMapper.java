package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.dto.request.OrderItemRequest;
import com.enigma.lastbite.dto.request.OrderRequest;
import com.enigma.lastbite.dto.response.OrderItemResponse;
import com.enigma.lastbite.dto.response.OrderResponse;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.dto.response.ReportResponse;
import com.enigma.lastbite.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderItem toOrderItemEntity(OrderItemRequest itemRequest, MenuItem menuItem) {
        return OrderItem.builder()
                .menuItem(menuItem)
                .quantityPurchased(itemRequest.getQuantity())
                .pricePerItem(menuItem.getDiscountedPrice())
                .build();
    }

    public static Order toOrderEntity(User customer, SellerProfile seller, List<OrderItem> items, BigDecimal totalAmount, String verificationCode) {
        Order order = Order.builder()
                .customer(customer)
                .sellerProfile(seller)
                .orderItems(items)
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING_PAYMENT)
                .verificationCode(verificationCode)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        items.forEach(item -> item.setOrder(order));
        return order;
    }

    public static OrderResponse toResponse(Order order) {
        return toResponse(order, null);
    }

    public static OrderResponse toResponse(Order order, PaymentResponse payment) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses;
        if (order.getOrderItems() != null) {
            itemResponses = order.getOrderItems().stream()
                    .map(OrderMapper::toItemResponse)
                    .collect(Collectors.toList());
        } else {
            itemResponses = Collections.emptyList();
        }

        return OrderResponse.builder()
                .orderId(order.getId())
                .customerId(order.getCustomer().getId())
                .sellerId(order.getSellerProfile().getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getOrderStatus())
                .orderItems(itemResponses)
                .verificationCode(order.getVerificationCode())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .payment(payment)
                .build();
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }
        return OrderItemResponse.builder()
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantityPurchased())
                .pricePerItem(item.getPricePerItem())
                .build();
    }

    public static ReportResponse toResponse(
            long totalCustomer,
            long totalSeller,
            long totalSuccessTx,
            BigDecimal totalSuccessAmount
    ) {
        return ReportResponse.builder()
                .totalCustomer(totalCustomer)
                .totalSeller(totalSeller)
                .totalSuccessTx(totalSuccessTx)
                .totalSuccessAmount(totalSuccessAmount)
                .build();
    }
}