package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.UpdateSellerRequest;
import com.enigma.lastbite.dto.response.SellerResponse;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.SellerMapper;
import com.enigma.lastbite.repository.SellerProfileRepository;
import com.enigma.lastbite.service.SellerService;
import com.enigma.lastbite.specification.SellerSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerServiceImpl implements SellerService {
    SellerProfileRepository sellerProfileRepository;

    @Override
    public SellerResponse getById(String id) {
        SellerProfile sellerProfile = findBySellerId(id);
        return SellerMapper.toSellerResponse(sellerProfile);
    }

    @Override
    public SellerResponse getByUserId(String userId) {
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return SellerMapper.toSellerResponse(sellerProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerResponse> getAll(String storeName, UserStatus status, int page, int size, String sortField, String sortDir) {
        Sort sort = Sort.by(
                "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortField == null || sortField.isBlank() ? "storeName" : sortField
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<SellerProfile> spec = SellerSpecification.build(storeName, status);

        Page<SellerProfile> sellerProfiles = sellerProfileRepository.findAll(spec, pageable);

        return sellerProfiles.map(SellerMapper::toSellerResponse);
    }

    @Override
    public SellerResponse update(String id, UpdateSellerRequest request) {
        SellerProfile sellerProfile = findBySellerId(id);
        if(request.getStoreName() != null) {
            sellerProfile.setStoreName(request.getStoreName());
        }
        if(request.getStoreDescription() != null) {
            sellerProfile.setStoreDescription(request.getStoreDescription());
        }
        if(request.getAddress() != null) {
            sellerProfile.setAddress(request.getAddress());
        }
        if(request.getLatitude() != null) {
            sellerProfile.setLatitude(request.getLatitude());
        }
        if(request.getLongitude() != null) {
            sellerProfile.setLongitude(request.getLongitude());
        }
        if(request.getStatus() != null) {
            sellerProfile.setStatus(request.getStatus());
        }

        return SellerMapper.toSellerResponse(sellerProfile);
    }


    public SellerProfile findBySellerId(String id) {
        return sellerProfileRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND));
    }

    public SellerProfile save(SellerProfile sellerProfile) {
        return sellerProfileRepository.save(sellerProfile);
    }


}
