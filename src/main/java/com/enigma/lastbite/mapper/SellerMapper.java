package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.response.SellerResponse;
import com.enigma.lastbite.entity.SellerProfile;

public class SellerMapper {
    public static SellerResponse toSellerResponse(SellerProfile sellerProfile) {
        return SellerResponse.builder()
                .id(sellerProfile.getId())
                .userId(sellerProfile.getUser().getId().toString())
                .username(sellerProfile.getUser().getUsername())
                .email(sellerProfile.getUser().getEmail())
                .storeName(sellerProfile.getStoreName())
                .storeDescription(sellerProfile.getStoreDescription())
                .address(sellerProfile.getAddress())
                .latitude(sellerProfile.getLatitude())
                .longitude(sellerProfile.getLongitude())
                .status(sellerProfile.getStatus())
                .balance(sellerProfile.getBalance())
                .createdAt(sellerProfile.getCreatedAt())
                .updatedAt(sellerProfile.getUpdatedAt())
                .build();
    }
}
