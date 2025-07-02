package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.PaymentRequest;
import com.enigma.lastbite.dto.response.PaymentResponse;
import java.util.Map;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    void handleMidtransNotification(Map<String, Object> payload);
}