package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {
    private String orderId;
    private OrderStatus newStatus;
}
