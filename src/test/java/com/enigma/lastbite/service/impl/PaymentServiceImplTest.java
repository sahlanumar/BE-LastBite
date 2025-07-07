
package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.PaymentStatus;
import com.enigma.lastbite.dto.request.PaymentRequest;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.entity.Order;
import com.enigma.lastbite.entity.Payment;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.repository.PaymentRepository;
import com.enigma.lastbite.service.OrderService;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private MidtransSnapApi midtransSnapApi;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId("userId");
        user.setFullName("Test User");
        user.setEmail("test@example.com");

        order = new Order();
        order.setId("orderId");
        order.setCustomer(user);
        order.setTotalAmount(BigDecimal.valueOf(100000));

        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId("orderId");
    }

    @Test
    void createPayment_Success() throws MidtransError {
        when(orderService.findOrderByIdOrThrow("orderId")).thenReturn(order);
        when(midtransSnapApi.createTransactionToken(any())).thenReturn("transactionToken");
        when(midtransSnapApi.createTransactionRedirectUrl(any())).thenReturn("redirectUrl");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        PaymentResponse response = paymentService.createPayment(paymentRequest);

        assertNotNull(response);
        assertEquals("transactionToken", response.getToken());
        assertEquals("redirectUrl", response.getRedirectUrl());
    }

    @Test
    void handleMidtransNotification_Settlement() {
        Payment payment = new Payment();
        payment.setOrder(order);

        Map<String, Object> payload = new HashMap<>();
        payload.put("order_id", "orderId");
        payload.put("transaction_status", "settlement");
        payload.put("fraud_status", "accept");
        payload.put("payment_type", "credit_card");

        when(paymentRepository.findByOrder_Id("orderId")).thenReturn(Optional.of(payment));
        when(orderService.findOrderByIdOrThrow("orderId")).thenReturn(order);

        paymentService.handleMidtransNotification(payload);

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }
}
