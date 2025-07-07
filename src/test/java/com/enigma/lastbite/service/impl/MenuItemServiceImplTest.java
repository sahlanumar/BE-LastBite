package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.ListingStatus;
import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.MenuItemCreateRequest;
import com.enigma.lastbite.dto.request.MenuItemUpdateRequest;
import com.enigma.lastbite.dto.response.MenuItemResponse;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.MenuMapper;
import com.enigma.lastbite.repository.MenuItemRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceImplTest {

    @Mock private MenuItemRepository menuItemRepository;
    @Mock private SellerService sellerService;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserService userService;
    @Mock private MenuItemReviewService menuItemReviewService;
    @Mock private OrderService orderService;
    @Mock private CloudinaryService cloudinaryService;

    @InjectMocks
    private MenuItemServiceImpl menuItemService;

    private SellerProfile dummySeller;
    private MenuItem dummyItem;

    @BeforeEach
    void setUp() {
        dummySeller = SellerProfile.builder()
                .id("seller-1")
                .status(UserStatus.ACTIVE)
                .build();

        dummyItem = MenuItem.builder()
                .id("menu-1")
                .name("Bakso")
                .sellerProfile(dummySeller)
                .quantityAvailable(10)
                .displayStartTime(LocalDateTime.now().minusHours(1))
                .displayEndTime(LocalDateTime.now().plusHours(2))
                .build();
    }

    @Test
    void create_ValidRequest_ReturnsResponse() {
        // Given
        MenuItemCreateRequest request = MenuItemCreateRequest.builder()
                .name("Bakso")
                .sellerProfileId("seller-1")
                .originalPrice(BigDecimal.valueOf(15000))
                .discountedPrice(BigDecimal.valueOf(10000))
                .build();

        when(sellerService.findBySellerId("seller-1")).thenReturn(dummySeller);

        try (MockedStatic<MenuMapper> mockedMapper = mockStatic(MenuMapper.class)) {
            mockedMapper.when(() -> MenuMapper.toMenuItemEntity(request, dummySeller)).thenReturn(dummyItem);
            mockedMapper.when(() -> MenuMapper.toMenuItemResponse(dummyItem)).thenReturn(
                    MenuItemResponse.builder().id("menu-1").name("Bakso").build());

            // When
            MenuItemResponse response = menuItemService.create(request);

            // Then
            verify(menuItemRepository).save(dummyItem);
            assertEquals("menu-1", response.getId());
        }
    }

    @Test
    void create_SellerNotActive_ThrowsException() {
        SellerProfile inactive = SellerProfile.builder()
                .id("seller-1")
                .status(UserStatus.INACTIVE)
                .build();
        when(sellerService.findBySellerId("seller-1")).thenReturn(inactive);

        MenuItemCreateRequest request = MenuItemCreateRequest.builder()
                .sellerProfileId("seller-1")
                .build();

        CustomException ex = assertThrows(CustomException.class, () -> menuItemService.create(request));
        assertEquals(ErrorCode.SELLER_NOT_ACTIVE, ex.getErrorCode());
    }

    @Test
    void getById_Found_ReturnsResponse() {
        when(menuItemRepository.findById("menu-1")).thenReturn(Optional.of(dummyItem));
        try (MockedStatic<MenuMapper> mockedMapper = mockStatic(MenuMapper.class)) {
            mockedMapper.when(() -> MenuMapper.toMenuItemResponse(dummyItem))
                    .thenReturn(MenuItemResponse.builder().id("menu-1").build());

            MenuItemResponse response = menuItemService.getById("menu-1");
            assertEquals("menu-1", response.getId());
        }
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(menuItemRepository.findById("menu-999")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> menuItemService.getById("menu-999"));
    }

    @Test
    void update_ValidMenuItem_SetsAvailableStatus() {
        dummyItem.setQuantityAvailable(5);
        dummyItem.setDisplayEndTime(LocalDateTime.now().plusHours(2));
        dummyItem.setDisplayStartTime(LocalDateTime.now().minusHours(1));

        MenuItemUpdateRequest updateRequest = MenuItemUpdateRequest.builder().name("Updated Bakso").build();

        when(menuItemRepository.findById("menu-1")).thenReturn(Optional.of(dummyItem));
        when(menuItemRepository.save(any())).thenReturn(dummyItem);

        try (MockedStatic<MenuMapper> mockedMapper = mockStatic(MenuMapper.class)) {
            mockedMapper.when(() -> MenuMapper.updateFromDto(dummyItem, updateRequest)).thenAnswer(i -> null);
            mockedMapper.when(() -> MenuMapper.toMenuItemResponse(dummyItem))
                    .thenReturn(MenuItemResponse.builder().id("menu-1").name("Updated Bakso").build());

            MenuItemResponse updated = menuItemService.update("menu-1", updateRequest);

            assertEquals(ListingStatus.AVAILABLE, dummyItem.getStatus());
            assertEquals("Updated Bakso", updated.getName());
        }
    }

    @Test
    void deleteById_ValidId_Success() {
        when(menuItemRepository.findById("menu-1")).thenReturn(Optional.of(dummyItem));
        menuItemService.deleteById("menu-1");
        verify(menuItemRepository).delete(dummyItem);
    }

    @Test
    void averageRatingBySellerProfileId_CallsRepository() {
        when(menuItemRepository.findAverageRatingBySellerProfileId("seller-1")).thenReturn(4.5);
        Double result = menuItemService.averageRatingBySellerProfileId("seller-1");
        assertEquals(4.5, result);
    }
}
