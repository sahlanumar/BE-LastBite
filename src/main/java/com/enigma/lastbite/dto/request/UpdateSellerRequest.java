package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSellerRequest {
    private String storeName;
    private String storeDescription;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private UserStatus status;
}
