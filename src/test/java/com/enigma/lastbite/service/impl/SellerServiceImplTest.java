
package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.SellerUpdateRequest;
import com.enigma.lastbite.dto.response.SellerResponse;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.repository.SellerProfileRepository;
import com.enigma.lastbite.service.OrderService;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {

    @Mock
    private SellerProfileRepository sellerProfileRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private SellerServiceImpl sellerService;

    private SellerProfile sellerProfile;
    private SellerUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        sellerProfile = new SellerProfile();
        sellerProfile.setId("sellerId");
        sellerProfile.setStoreName("Toko Sejahtera");
        sellerProfile.setStatus(UserStatus.ACTIVE);

        updateRequest = new SellerUpdateRequest();
        updateRequest.setStoreName("Toko Makmur");
    }

    @Test
    void getById_Success() {
        when(sellerProfileRepository.findById("sellerId")).thenReturn(Optional.of(sellerProfile));
        when(orderService.countCompletedOrdersBySellerId("sellerId")).thenReturn(10L);

        SellerResponse response = sellerService.getById("sellerId");

        assertNotNull(response);
        assertEquals("Toko Sejahtera", response.getStoreName());
        assertEquals(10L, response.getTotalOrders());
    }

    @Test
    void getAll_Success() {
        Page<SellerProfile> page = new PageImpl<>(Collections.singletonList(sellerProfile));
        when(sellerProfileRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<SellerResponse> response = sellerService.getAll(null, null, 0, 10, "storeName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    @Test
    void update_Success() {
        when(sellerProfileRepository.findById("sellerId")).thenReturn(Optional.of(sellerProfile));
        when(sellerProfileRepository.save(any(SellerProfile.class))).thenReturn(sellerProfile);

        SellerResponse response = sellerService.update("sellerId", updateRequest);

        assertNotNull(response);
        assertEquals("Toko Makmur", response.getStoreName());
    }
}
