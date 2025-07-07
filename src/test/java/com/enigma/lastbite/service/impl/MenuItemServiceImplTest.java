
package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.dto.request.MenuItemCreateRequest;
import com.enigma.lastbite.dto.response.MenuItemResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.repository.MenuItemRepository;
import com.enigma.lastbite.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private SellerService sellerService;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    private MenuItem menuItem;
    private SellerProfile sellerProfile;
    private MenuItemCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        sellerProfile = new SellerProfile();
        sellerProfile.setId("sellerId");

        menuItem = new MenuItem();
        menuItem.setId("menuItemId");
        menuItem.setName("Nasi Goreng");
        menuItem.setOriginalPrice(BigDecimal.valueOf(10000));
        menuItem.setDiscountedPrice(BigDecimal.valueOf(10000));
        menuItem.setSellerProfile(sellerProfile);

        createRequest = new MenuItemCreateRequest();
        createRequest.setName("Nasi Goreng");
        menuItem.setOriginalPrice(BigDecimal.valueOf(10000));
        menuItem.setDiscountedPrice(BigDecimal.valueOf(10000));
        createRequest.setSellerProfileId("sellerId");
    }

    @Test
    void create_Success() {
        when(sellerService.findBySellerId("sellerId")).thenReturn(sellerProfile);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        MenuItemResponse response = menuItemService.create(createRequest);

        assertNotNull(response);
        assertEquals("Nasi Goreng", response.getName());
    }

    @Test
    void getById_Success() {
        when(menuItemRepository.findById("menuItemId")).thenReturn(Optional.of(menuItem));

        MenuItemResponse response = menuItemService.getById("menuItemId");

        assertNotNull(response);
        assertEquals("menuItemId", response.getId());
    }

    @Test
    void getAll_Success() {
        Page<MenuItem> page = new PageImpl<>(Collections.singletonList(menuItem));
        when(menuItemRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<MenuItemResponse> response = menuItemService.getAll(
                null, null, null, null, null, null, null, null, 0, 10, "name", "asc", null, null);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }
}
