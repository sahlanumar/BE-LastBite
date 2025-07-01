package com.enigma.lastbite.service;

import com.enigma.lastbite.constant.ListingStatus;
import com.enigma.lastbite.dto.request.CreateMenuItemRequest;
import com.enigma.lastbite.dto.request.UpdateMenuItemRequest;
import com.enigma.lastbite.dto.response.MenuItemResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface MenuItemService {

    MenuItemResponse create(CreateMenuItemRequest request);

    MenuItemResponse getById(String id);

    Page<MenuItemResponse> getAll(
            String name,
            String sellerId,
            BigDecimal maxPrice,
            BigDecimal minPrice,
            Boolean isAvailable,
            ListingStatus status,
            int page,
            int size,
            String sortField,
            String sortDir
    );

    Page<MenuItemResponse> getAllByLogin(
            String name, BigDecimal maxPrice, BigDecimal minPrice,
            Boolean isAvailable, ListingStatus status, int page, int size,
            String sortField, String sortDir);

    MenuItemResponse update(String id, UpdateMenuItemRequest request);

    void deleteById(String id);
}