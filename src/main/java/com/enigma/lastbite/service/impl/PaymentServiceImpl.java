package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.PaymentStatus;
import com.enigma.lastbite.dto.request.PaymentRequest;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.Order;
import com.enigma.lastbite.entity.OrderItem;
import com.enigma.lastbite.entity.Payment;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.PaymentMapper;
import com.enigma.lastbite.repository.PaymentRepository;
import com.enigma.lastbite.service.OrderService;
import com.enigma.lastbite.service.PaymentService;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransSnapApi;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final MidtransSnapApi midtransSnapApi;
    private final MenuItemServiceImpl menuItemService;
    private final SimpMessagingTemplate messagingTemplate;
    private final OrderServiceImpl orderServiceImpl;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public PaymentResponse createPayment(PaymentRequest request) {
        Order order = orderService.findOrderByIdOrThrow(request.getOrderId());

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", order.getId());
        transactionDetails.put("gross_amount", order.getTotalAmount().longValue());

        Map<String, String> customerDetails = new HashMap<>();
        customerDetails.put("first_name", order.getCustomer().getFullName());
        customerDetails.put("email", order.getCustomer().getEmail());

        Map<String, Object> params = new HashMap<>();
        params.put("transaction_details", transactionDetails);
        params.put("customer_details", customerDetails);

        try {
            String transactionToken = midtransSnapApi.createTransactionToken(params);
            String redirectUrl = midtransSnapApi.createTransactionRedirectUrl(params);
            String transactionId = (String) transactionDetails.get("order_id");

            Payment payment = PaymentMapper.toPaymentEntity(order, transactionId);
            paymentRepository.save(payment);

            return PaymentMapper.toResponse(payment, transactionToken, redirectUrl);

        } catch (MidtransError e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Midtrans error: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void handleMidtransNotification(Map<String, Object> payload) {
        String orderId = (String) payload.get("order_id");
        String transactionStatus = (String) payload.get("transaction_status");
        String fraudStatus = (String) payload.get("fraud_status");
        String paymentType = (String) payload.get("payment_type");

        Payment payment = paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Payment for order id " + orderId + " not found"));

        Order order = orderService.findOrderByIdOrThrow(orderId);

        if ("capture".equalsIgnoreCase(transactionStatus) || "settlement".equalsIgnoreCase(transactionStatus)) {
            if ("accept".equalsIgnoreCase(fraudStatus)) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaymentType(paymentType);
                paymentRepository.save(payment);
                orderService.updateStatusToPaid(orderId);
            }
        } else if ("expire".equalsIgnoreCase(transactionStatus)) {
            payment.setStatus(PaymentStatus.EXPIRED);
            for (OrderItem orderItem : order.getOrderItems()) {
                MenuItem menuItem = menuItemService.findById(orderItem.getMenuItem().getId());
                menuItem.setQuantityAvailable(menuItem.getQuantityAvailable() + orderItem.getQuantityPurchased());
                menuItemService.save(menuItem);
            }

            paymentRepository.save(payment);
            orderService.updateStatusToCancelled(orderId);
        } else if ("deny".equalsIgnoreCase(transactionStatus) || "cancel".equalsIgnoreCase(transactionStatus)) {
            payment.setStatus(PaymentStatus.FAILED);
            for (OrderItem orderItem : order.getOrderItems()) {
                MenuItem menuItem = menuItemService.findById(orderItem.getMenuItem().getId());
                menuItem.setQuantityAvailable(menuItem.getQuantityAvailable() + orderItem.getQuantityPurchased());
                menuItemService.save(menuItem);
            }
            paymentRepository.save(payment);
            orderService.updateStatusToCancelled(orderId);
        }
//
//        PaymentResponse paymentResponse = PaymentMapper.toResponse(payment, "", "");
//        messagingTemplate.convertAndSend("/topic/payment/" + order.getId(), paymentResponse);
    }
}