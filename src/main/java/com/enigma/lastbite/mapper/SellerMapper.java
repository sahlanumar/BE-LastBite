package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.UserStatus;
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
            if(request.getStatus().equals(UserStatus.CANCELLED)){
                sellerProfile.setCancelReason(request.getCancelReason());
            }
        }
        if (request.getStoreImageUrl() != null) {
            sellerProfile.setStoreImageUrl(request.getStoreImageUrl());
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
                .cancellationReason(sellerProfile.getCancelReason())
                .averageRating(sellerProfile.getAverageRatingMenu())
                .balance(sellerProfile.getBalance())
                .phoneNumber(sellerProfile.getUser().getPhoneNumber())
                .storeImageUrl(sellerProfile.getStoreImageUrl())
                .profileImageUrl(sellerProfile.getUser().getProfileImageUrl())
                .createdAt(sellerProfile.getCreatedAt())
                .updatedAt(sellerProfile.getUpdatedAt())
                .build();
    }
    public static SellerResponse toSellerResponse(SellerProfile sellerProfile,Long totalOrder) {
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
                .cancellationReason(sellerProfile.getCancelReason())
                .averageRating(sellerProfile.getAverageRatingMenu())
                .balance(sellerProfile.getBalance())
                .phoneNumber(sellerProfile.getUser().getPhoneNumber())
                .storeImageUrl(sellerProfile.getStoreImageUrl())
                .profileImageUrl(sellerProfile.getUser().getProfileImageUrl())
                .createdAt(sellerProfile.getCreatedAt())
                .updatedAt(sellerProfile.getUpdatedAt())
                .totalOrders(totalOrder)
                .build();
    }
}