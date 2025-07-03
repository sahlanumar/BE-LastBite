package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.PaymentStatus;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.entity.Order;
import com.enigma.lastbite.entity.Payment;

import java.time.LocalDateTime;

public class PaymentMapper {

    public static Payment toPaymentEntity(Order order, String midtransTransactionId) {
        return Payment.builder()
                .order(order)
                .midtransTransactionId(midtransTransactionId)
                .amount(order.getTotalAmount())
                .status(PaymentStatus.PENDING)
                .transactionTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static PaymentResponse toResponse(Payment payment, String token, String redirectUrl) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .midtransTransactionId(payment.getMidtransTransactionId())
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .transactionStatus(payment.getStatus().name())
                .transactionTime(payment.getTransactionTime())
                .token(token)
                .redirectUrl(redirectUrl)
                .build();
    }

    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .midtransTransactionId(payment.getMidtransTransactionId())
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .transactionStatus(payment.getStatus().name())
                .transactionTime(payment.getTransactionTime())
                .build();
    }
}