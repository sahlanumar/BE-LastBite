package com.enigma.lastbite.service;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.dto.request.OrderFilterRequest;
import com.enigma.lastbite.dto.request.OrderRequest;
import com.enigma.lastbite.dto.request.ReportFilterRequest;
import com.enigma.lastbite.dto.request.VerifyOrderRequest;
import com.enigma.lastbite.dto.response.OrderResponse;
import com.enigma.lastbite.dto.response.ReportResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    List<Order> findAllCompletedOrdersByCustomerId(String customerId); // Menambahkan metode ini di OrderService/Repository: findAllCompletedOrdersByCustomerId

    List<Order> findListOrderByCustomerIdAndMenuItemId(String customerId, String menuItemId);

    Page<OrderResponse> getAllOrders(OrderFilterRequest filter, int page, int size, String sortField, String sortDir);

    OrderResponse createOrder(OrderRequest request);

    OrderResponse getOrderById(String orderId);

    OrderResponse acceptOrder(String orderId);

    OrderResponse markAsReadyForPickup(String orderId);

    OrderResponse cancelOrder(String orderId);

    OrderResponse verifyAndCompleteOrder(String orderId, VerifyOrderRequest request);

    void updateStatusToPaid(String orderId);

    Page<OrderResponse> getAllOrdersForCustomer(Pageable pageable);

    Page<OrderResponse> getAllOrdersForSeller(Pageable pageable);

    Order findOrderByIdOrThrow(String id);

    ReportResponse getReport(ReportFilterRequest filter);
}