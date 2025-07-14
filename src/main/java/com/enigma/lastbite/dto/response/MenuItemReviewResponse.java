package com.enigma.lastbite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuItemReviewResponse {
    private String id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private String profileImageUrl;
    private String orderId;
    private String menuItemId;
    private String menuItemName;
    private String customerId;
    private String customerName;
}