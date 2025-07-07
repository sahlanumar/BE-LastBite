package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerFilterRequest {
    private String storeName;
    private UserStatus status;
}

