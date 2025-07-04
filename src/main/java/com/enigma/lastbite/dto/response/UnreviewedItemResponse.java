package com.enigma.lastbite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnreviewedItemResponse {
    private String orderId;
    private MenuItemResponse menuItem;
}