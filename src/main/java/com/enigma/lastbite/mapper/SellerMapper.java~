package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.request.SellerUpdateRequest;
import com.enigma.lastbite.dto.response.SellerResponse;
import com.enigma.lastbite.entity.SellerProfile;

public class SellerMapper {

    public static void updateFromDto(SellerProfile sellerProfile, SellerUpdateRequest request) {
        if (request.getStoreName() != null) {
            sellerProfile.setStoreName(request.getStoreName());
        }
        if (request.getStoreDescription() != null) {
            sellerProfile.setStoreDescription(request.getStoreDescription());
        }
        if (request.getAddress() != null) {
            sellerProfile.setAddress(request.getAddress());
        }
        if (request.getLatitude() != null) {
            sellerProfile.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            sellerProfile.setLongitude(request.getLongitude());
        }
        if (request.getStatus() != null) {
            sellerProfile.setStatus(request.getStatus());
        }
    }

    public static SellerResponse toSellerResponse(SellerProfile sellerProfile) {
        return SellerResponse.builder()
                .id(sellerProfile.getId())
                .userId(sellerProfile.getUser().getId())
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