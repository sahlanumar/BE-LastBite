package com.enigma.lastbite.controller;

import com.enigma.lastbite.dto.request.OrderRequest;
import com.enigma.lastbite.dto.request.VerifyOrderRequest;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.OrderResponse;
import com.enigma.lastbite.service.OrderService;
import com.enigma.lastbite.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /* ------------------------------------------------------------------ *
     * CUSTOMER + SELLER INDEPENDENT ENDPOINTS                            *
     * ------------------------------------------------------------------ */

    @PostMapping
    public ResponseEntity<CommonResponse<OrderResponse>> createOrder(
            @RequestBody OrderRequest request) {

        OrderResponse response = orderService.createOrder(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, "Order created", response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<OrderResponse>> getOrderById(@PathVariable String id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Order found", response);
    }

    /* ------------------------------------------------------------------ *
     * CUSTOMER‑ONLY ENDPOINTS                                            *
     * ------------------------------------------------------------------ */

    @GetMapping("/customer/me")
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getMyOrdersAsCustomer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String baseUrl
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortField)
        );

        Page<OrderResponse> orderPage = orderService.getAllOrdersForCustomer(pageable);

        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Orders fetched",
                orderPage.getContent(),
                orderPage,
                baseUrl,
                Collections.emptyMap(),
                sortField,
                sortDir
        );
    }

    /* ------------------------------------------------------------------ *
     * SELLER‑ONLY ENDPOINTS                                              *
     * ------------------------------------------------------------------ */

    @GetMapping("/seller/me")
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getMyOrdersAsSeller(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String baseUrl
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.fromString(sortDir), sortField)
        );

        Page<OrderResponse> orderPage = orderService.getAllOrdersForSeller(pageable);

        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                "Orders fetched",
                orderPage.getContent(),
                orderPage,
                baseUrl,
                Collections.emptyMap(),
                sortField,
                sortDir
        );
    }

    @PutMapping("/{orderId}/accept")
    public ResponseEntity<CommonResponse<OrderResponse>> acceptOrder(@PathVariable String orderId) {
        OrderResponse response = orderService.acceptOrder(orderId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Order accepted", response);
    }

    @PutMapping("/{orderId}/ready")
    public ResponseEntity<CommonResponse<OrderResponse>> markAsReady(
            @PathVariable String orderId) {

        OrderResponse response = orderService.markAsReadyForPickup(orderId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Order marked as ready for pickup", response);
    }

    /* ------------------------------------------------------------------ *
     * STATUS & VERIFICATION ENDPOINTS                                    *
     * ------------------------------------------------------------------ */

    @PutMapping("/{orderId}/paid")
    public ResponseEntity<CommonResponse<Void>> updateStatusToPaid(@PathVariable String orderId) {
        orderService.updateStatusToPaid(orderId);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Order status updated to PAID", null);
    }

    @PutMapping("/{orderId}/complete")
    public ResponseEntity<CommonResponse<OrderResponse>> verifyAndCompleteOrder(
            @PathVariable String orderId,
            @RequestBody VerifyOrderRequest request) {

        OrderResponse response = orderService.verifyAndCompleteOrder(orderId, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Order completed", response);
    }
}
