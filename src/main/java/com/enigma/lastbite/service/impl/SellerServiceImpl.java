package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.SellerUpdateRequest;
import com.enigma.lastbite.dto.response.SellerResponse;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.SellerMapper;
import com.enigma.lastbite.repository.SellerProfileRepository;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.service.OrderService;
import com.enigma.lastbite.service.SellerService;
import com.enigma.lastbite.service.UserService;
import com.enigma.lastbite.specification.SellerSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class SellerServiceImpl implements SellerService {
    private final SellerProfileRepository sellerProfileRepository;
    private final UserService userService;
    private final MenuItemService menuItemService;
    private final OrderService orderService;

    public SellerServiceImpl(SellerProfileRepository sellerProfileRepository,@Lazy MenuItemService menuItemService,@Lazy UserService userService,@Lazy OrderService orderService) {
        this.userService = userService;
        this.sellerProfileRepository = sellerProfileRepository;
        this.menuItemService = menuItemService;
        this.orderService = orderService;
    }

    @Override
    public SellerResponse getById(String id) {
        SellerProfile sellerProfile = findBySellerId(id);
        Long totalOrder = orderService.countCompletedOrdersBySellerId(id);
        return SellerMapper.toSellerResponse(sellerProfile, totalOrder);
    }

    @Override
    public SellerResponse getByUserId(String userId) {
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Long totalOrder = orderService.countCompletedOrdersBySellerId(sellerProfile.getId());
        return SellerMapper.toSellerResponse(sellerProfile, totalOrder);
    }

    @Override
    public SellerProfile getSellerProfileByUserId(String userId) {
        return sellerProfileRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
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
    @Transactional
    public SellerResponse update(String id, SellerUpdateRequest request) {
        SellerProfile sellerProfile = findBySellerId(id);

        SellerMapper.updateFromDto(sellerProfile, request);

        sellerProfileRepository.save(sellerProfile);

        return SellerMapper.toSellerResponse(sellerProfile);
    }

    public SellerProfile findBySellerId(String id) {
        if(!sellerProfileRepository.existsById(id)) {
            User user = userService.findById(id);
            return sellerProfileRepository.findByUserId(user.getId()).orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND));
        }
        return sellerProfileRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND));
    }

    @Override
    public SellerProfile save(SellerProfile sellerProfile) {
        return sellerProfileRepository.save(sellerProfile);
    }

    @Override
    public long countByStatus(UserStatus status) {
        return sellerProfileRepository.countByStatus(status);
    }


}