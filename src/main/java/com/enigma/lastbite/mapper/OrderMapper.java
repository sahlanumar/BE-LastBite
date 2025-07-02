package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.response.OrderItemResponse;
import com.enigma.lastbite.dto.response.OrderResponse;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.entity.Order;
import com.enigma.lastbite.entity.OrderItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

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
}