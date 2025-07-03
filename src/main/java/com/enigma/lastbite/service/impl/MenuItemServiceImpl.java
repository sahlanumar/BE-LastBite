package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.ListingStatus;
import com.enigma.lastbite.dto.request.CreateMenuItemRequest;
import com.enigma.lastbite.dto.request.UpdateMenuItemRequest;
import com.enigma.lastbite.dto.response.MenuItemResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.MenuMapper;
import com.enigma.lastbite.repository.MenuItemRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.service.SellerService;
import com.enigma.lastbite.service.UserService;
import com.enigma.lastbite.specification.MenuItemSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final SellerService sellerService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Transactional
    @Override
    public MenuItemResponse create(CreateMenuItemRequest request) {
        SellerProfile sellerProfile = sellerService.findBySellerId(request.getSellerProfileId());
        MenuItem menuItem = MenuMapper.toMenuItemEntity(request, sellerProfile);
        menuItemRepository.save(menuItem);
        return MenuMapper.toMenuItemResponse(menuItem);
    }

    @Transactional(readOnly = true)
    @Override
    public MenuItemResponse getById(String id) {
        MenuItem menuItem = findByIdOrThrowNotFound(id);
        return MenuMapper.toMenuItemResponse(menuItem);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MenuItemResponse> getAll(
            String name, String sellerId, BigDecimal maxPrice, BigDecimal minPrice,
            Boolean isAvailable, ListingStatus status, int page, int size,
            String sortField, String sortDir, Double userLat, Double userLon) {

        Pageable pageable = PageRequest.of(page, size);
        Specification<MenuItem> spec = MenuItemSpecification.getSpecification(
                name, sellerId, maxPrice, minPrice, isAvailable, status
        );

        Page<MenuItem> menuItems = menuItemRepository.findAll(spec, Pageable.unpaged());

        List<MenuItemResponse> responses = menuItems.getContent().stream()
                .map(item -> MenuMapper.toMenuItemResponse(item, userLat, userLon))
                .toList();

        if ("distance".equalsIgnoreCase(sortField) && userLat != null && userLon != null) {
            responses = responses.stream()
                    .sorted((a, b) -> {
                        if (a.getDistanceKm() == null) return 1;
                        if (b.getDistanceKm() == null) return -1;
                        return "asc".equalsIgnoreCase(sortDir)
                                ? Double.compare(a.getDistanceKm(), b.getDistanceKm())
                                : Double.compare(b.getDistanceKm(), a.getDistanceKm());
                    })
                    .toList();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());
        List<MenuItemResponse> pagedList = responses.subList(start, end);

        return new PageImpl<>(pagedList, pageable, responses.size());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<MenuItemResponse> getAllByLogin(
            String name, BigDecimal maxPrice, BigDecimal minPrice,
            Boolean isAvailable, ListingStatus status, int page, int size,
            String sortField, String sortDir) {

        Sort sort = Sort.by(
                "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortField == null || sortField.isBlank() ? "name" : sortField
        );
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User user = userService.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String sellerId = sellerService.getSellerProfileByUserId(user.getId()).getId();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<MenuItem> spec = MenuItemSpecification.getSpecification(
                name, sellerId, maxPrice, minPrice, isAvailable, status);

        Page<MenuItem> menuItems = menuItemRepository.findAll(spec, pageable);
        return menuItems.map(MenuMapper::toMenuItemResponse);
    }

    @Transactional
    @Override
    public MenuItemResponse update(String id, UpdateMenuItemRequest request) {
        MenuItem menuItem = findByIdOrThrowNotFound(id);

        MenuMapper.updateFromDto(menuItem, request);

        ListingStatus status = ListingStatus.NOT_AVAILABLE;
        if (menuItem.getQuantityAvailable() > 0 && menuItem.getDisplayEndTime().isAfter(LocalDateTime.now()) && menuItem.getDisplayStartTime().isBefore(LocalDateTime.now())) {
            status = ListingStatus.AVAILABLE;
        }
        menuItem.setStatus(status);

        menuItemRepository.save(menuItem);
        return MenuMapper.toMenuItemResponse(menuItem);
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        MenuItem menuItem = findByIdOrThrowNotFound(id);
        menuItemRepository.delete(menuItem);
    }

    @Override
    public MenuItem save(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem findById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    private MenuItem findByIdOrThrowNotFound(String id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void updateExpiredMenuItems() {
        List<MenuItem> expiredItems = menuItemRepository.findAllByDisplayEndTimeBeforeAndQuantityAvailableGreaterThan(LocalDateTime.now(), 0);

        if (!expiredItems.isEmpty()) {
            for (MenuItem item : expiredItems) {
                item.setQuantityAvailable(0);
                item.setStatus(ListingStatus.NOT_AVAILABLE);
            }
            menuItemRepository.saveAll(expiredItems);
        }
    }
}