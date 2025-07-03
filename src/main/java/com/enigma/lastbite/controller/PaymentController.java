package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.ResponseMessage;
import com.enigma.lastbite.dto.request.PaymentRequest;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.PaymentResponse;
import com.enigma.lastbite.service.PaymentService;
import com.enigma.lastbite.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CommonResponse<PaymentResponse>> createPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_SAVE_DATA, response);
    }

    @PostMapping("/midtrans-notification")
    public ResponseEntity<CommonResponse<Void>> handleMidtransNotification(@RequestBody Map<String, Object> payload) {
        System.out.println("midtrans notification: " + payload);
        paymentService.handleMidtransNotification(payload);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_UPDATE_DATA, null);
    }
}
