package com.enigma.lastbite.dto.response;

import com.enigma.lastbite.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String orderId;
    private String customerId;
    private String sellerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String verificationCode;
    private String notes;
    private String customerName;
    private String imgProductUrl;
    private String storeName;
    private String longitude;
    private String latitude;
    private String urlMidtrans;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> orderItems;
    private PaymentResponse payment;
}
