package com.enigma.lastbite.service;

import com.enigma.lastbite.constant.ListingStatus;
import com.enigma.lastbite.dto.request.MenuItemCreateRequest;
import com.enigma.lastbite.dto.request.MenuItemUpdateRequest;
import com.enigma.lastbite.dto.response.MenuItemResponse;
import com.enigma.lastbite.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface MenuItemService {

    Double averageRatingBySellerProfileId(String sellerId);

    MenuItem save(MenuItem menuItem);

    MenuItemResponse create(MenuItemCreateRequest request, MultipartFile imageFile) ;

    MenuItemResponse getById(String id);

    Page<MenuItemResponse> getAll(
            String name, String sellerId, BigDecimal maxPrice, BigDecimal minPrice,
            Boolean isAvailable, ListingStatus status,
            Double minRating, Double maxRating, // parameter rating
            int page, int size,
            String sortField, String sortDir, Double userLat, Double userLon
    );

    Page<MenuItemResponse> getAllByLogin(
            String name, BigDecimal maxPrice, BigDecimal minPrice,
            Boolean isAvailable, ListingStatus status,
            // --- PERBAIKAN: Tambahkan parameter rating di signature ---
            Double minRating, Double maxRating,
            int page, int size,
            String sortField, String sortDir);

    MenuItemResponse update(String id, MenuItemUpdateRequest request);

    void deleteById(String id);

    MenuItem findById(String id);
}