package com.enigma.lastbite.service;

import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.SellerUpdateRequest;
import com.enigma.lastbite.dto.response.SellerResponse;
import com.enigma.lastbite.entity.SellerProfile;
import org.springframework.data.domain.Page;

public interface SellerService {
    SellerResponse getById(String id);

    SellerResponse getByUserId(String userId);

    SellerResponse getByLogin();

    SellerProfile getSellerProfileByUserId(String userId);

    Page<SellerResponse> getAll(
            String storeName,
            UserStatus status,
            int page,
            int size,
            String sortField,
            String sortDir);



    SellerResponse update(String id, SellerUpdateRequest request);

    SellerProfile findBySellerId(String id);

    SellerProfile save(SellerProfile sellerProfile);

    long countByStatus(UserStatus status);

}
