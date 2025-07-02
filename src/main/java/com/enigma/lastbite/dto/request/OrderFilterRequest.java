package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterRequest {
    private String customerId;
    private String sellerId;
    private OrderStatus status;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
}
