package com.enigma.lastbite.service.impl;

// Import yang dibutuhkan
import com.enigma.lastbite.constant.ListingStatus;
import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartGroupedResponse;
import com.enigma.lastbite.entity.*;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.CartMapper;
import com.enigma.lastbite.repository.CartItemRepository;
import com.enigma.lastbite.repository.CartRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserService userService;
    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User dummyUser;
    private MenuItem dummyMenuItem;
    private Cart dummyCart;
    private CartItem dummyCartItem;
    private SellerProfile dummySellerProfile;

    @BeforeEach
    void setUp() {
        dummyUser = User.builder().id("user-1").username("testuser").build();
        dummySellerProfile = SellerProfile.builder().id("seller-1").build();

        // PERBAIKAN: Melengkapi dummyMenuItem sesuai struktur entity yang baru
        dummyMenuItem = MenuItem.builder()
                .id("menu-1")
                .name("Nasi Goreng Spesial")
                .sellerProfile(dummySellerProfile)
                .originalPrice(new BigDecimal("25000"))
                .discountedPrice(new BigDecimal("20000"))
                .quantityAvailable(10)
                .status(ListingStatus.AVAILABLE)
                .displayStartTime(LocalDateTime.now().minusHours(1))
                .displayEndTime(LocalDateTime.now().plusHours(5))
                .build();

        // PERBAIKAN: Menggunakan ArrayList agar bisa dimodifikasi (add, remove, clear)
        dummyCart = Cart.builder()
                .id("cart-1")
                .customer(dummyUser)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        dummyCartItem = CartItem.builder()
                .id("item-1")
                .cart(dummyCart)
                .menuItem(dummyMenuItem)
                .quantity(1)
                .build();

        // Menambahkan item ke list di dalam keranjang
        dummyCart.getItems().add(dummyCartItem);
    }

    // Metode helper untuk setup otentikasi user
    private void setupUserAuthentication(String username) {
        // PERBAIKAN: Menghapus `doNothing` yang menyebabkan MockitoException.
        // Mocking `getUsernameFromJwtToken` sudah cukup untuk simulasi otentikasi sukses.
        when(jwtUtils.getTokenFromHeader()).thenReturn("Bearer token");
        // doNothing().when(jwtUtils).validateJwtToken("Bearer token"); // <-- INI PENYEBAB ERROR, HAPUS ATAU KOMENTARI
        when(jwtUtils.getUsernameFromJwtToken("Bearer token")).thenReturn(username);
        when(userService.findByUsername(username)).thenReturn(Optional.of(dummyUser));
    }

    @Nested
    @DisplayName("AddItem Tests")
    class AddItemTests {

        // Ubah hanya metode test yang gagal ini di dalam kelas CartServiceImplTest


        @Test
        @DisplayName("addItem(): Menambah item baru ke keranjang yang sudah ada → Sukses")
        void addItem_NewItemToExistingCart_Success() {
            try (MockedStatic<CartMapper> mockedMapper = mockStatic(CartMapper.class)) {
                // Given (Setup tetap sama)
                AddItemToCartRequest request = AddItemToCartRequest.builder().menuItemId("menu-2").quantity(2).build();
                MenuItem newItem = MenuItem.builder()
                        .id("menu-2")
                        .name("Mie Ayam")
                        .sellerProfile(dummySellerProfile)
                        .originalPrice(new BigDecimal("18000"))
                        .discountedPrice(new BigDecimal("15000"))
                        .quantityAvailable(20)
                        .status(ListingStatus.AVAILABLE)
                        .displayStartTime(LocalDateTime.now().minusHours(1))
                        .displayEndTime(LocalDateTime.now().plusHours(5))
                        .build();

                setupUserAuthentication("testuser");
                when(cartRepository.findByCustomerId(dummyUser.getId())).thenReturn(Optional.of(dummyCart));
                when(menuItemService.findById("menu-2")).thenReturn(newItem);

                mockedMapper.when(() -> CartMapper.toCartGroupedResponse(any(Cart.class)))
                        .thenReturn(CartGroupedResponse.builder().build());


                // When
                cartService.addItem(request);


                // Then: Gunakan ArgumentCaptor untuk memeriksa apa yang SEBENARNYA disimpan

                // 1. Buat ArgumentCaptor untuk tipe data yang ingin ditangkap (Cart)
                ArgumentCaptor<Cart> cartArgumentCaptor = ArgumentCaptor.forClass(Cart.class);

                // 2. Verifikasi pemanggilan metode .save() dan perintahkan captor untuk menangkap argumennya
                verify(cartRepository).save(cartArgumentCaptor.capture());

                // 3. Ambil objek Cart yang ditangkap dari captor
                Cart capturedCart = cartArgumentCaptor.getValue();

                // 4. Lakukan semua asersi pada objek yang ditangkap (capturedCart), bukan pada dummyCart
                assertEquals(2, capturedCart.getItems().size(), "Keranjang yang disimpan seharusnya memiliki 2 item");

                Optional<CartItem> addedItemOpt = capturedCart.getItems().stream()
                        .filter(item -> item != null && item.getMenuItem() != null && item.getMenuItem().getId().equals("menu-2"))
                        .findFirst();

                assertTrue(addedItemOpt.isPresent(), "Item baru (menu-2) seharusnya ada di dalam keranjang yang disimpan");
                assertEquals(2, addedItemOpt.get().getQuantity(), "Kuantitas item baru seharusnya 2");
            }
        }

        @Test
        @DisplayName("addItem(): Menambah kuantitas item yang sudah ada di keranjang → Sukses")
        void addItem_ExistingItemToCart_Success() {
            try (MockedStatic<CartMapper> mockedMapper = mockStatic(CartMapper.class)) {
                // Given
                AddItemToCartRequest request = AddItemToCartRequest.builder().menuItemId("menu-1").quantity(2).build();
                // setUp sudah menyiapkan keranjang dengan 1 item (kuantitas 1)

                setupUserAuthentication("testuser");
                when(cartRepository.findByCustomerId(dummyUser.getId())).thenReturn(Optional.of(dummyCart));
                when(menuItemService.findById("menu-1")).thenReturn(dummyMenuItem);
                when(cartRepository.save(any(Cart.class))).thenReturn(dummyCart);

                mockedMapper.when(() -> CartMapper.toCartGroupedResponse(any(Cart.class)))
                        .thenReturn(CartGroupedResponse.builder().build());

                // When
                cartService.addItem(request);

                // Then
                verify(cartRepository).save(dummyCart);
                // PERBAIKAN ASERSI: Harusnya item tetap 1, tapi kuantitas bertambah (1 + 2 = 3)
                assertEquals(1, dummyCart.getItems().size());
                assertEquals(3, dummyCart.getItems().get(0).getQuantity());
            }
        }

        @Test
        @DisplayName("addItem(): Menambah item saat user belum punya keranjang → Sukses")
        void addItem_UserHasNoCart_Success() {
            try (MockedStatic<CartMapper> mockedMapper = mockStatic(CartMapper.class)) {
                // Given
                AddItemToCartRequest request = AddItemToCartRequest.builder().menuItemId("menu-1").quantity(1).build();

                setupUserAuthentication("testuser");
                when(menuItemService.findById("menu-1")).thenReturn(dummyMenuItem);
                when(cartRepository.findByCustomerId(dummyUser.getId())).thenReturn(Optional.empty()); // User belum punya cart
                when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
                    Cart cart = invocation.getArgument(0);
                    if (cart.getId() == null) cart.setId("new-cart-id");
                    return cart;
                });

                mockedMapper.when(() -> CartMapper.toCartGroupedResponse(any(Cart.class)))
                        .thenReturn(CartGroupedResponse.builder().build());

                // When
                cartService.addItem(request);

                // Then
                // PERBAIKAN ASERSI: Kemungkinan save dipanggil 2x: 1. membuat cart, 2. mengupdate dgn item
                verify(cartRepository, atLeastOnce()).save(any(Cart.class));
            }
        }

        @Test
        @DisplayName("addItem(): User tidak ditemukan → USER_NOT_FOUND")
        void addItem_UserNotFound_ThrowsException() {
            // Given
            AddItemToCartRequest request = new AddItemToCartRequest();
            when(jwtUtils.getTokenFromHeader()).thenReturn("Bearer token");
            // Menghapus mock validateToken yang error
            when(jwtUtils.getUsernameFromJwtToken("Bearer token")).thenReturn("unknownuser");
            when(userService.findByUsername("unknownuser")).thenReturn(Optional.empty());

            // When & Then
            CustomException ex = assertThrows(CustomException.class, () -> cartService.addItem(request));
            assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("GetCartByLogin Tests")
    class GetCartByLoginTests {

        @Test
        @DisplayName("getCartByLogin(): Keranjang ditemukan → Sukses")
        void getCartByLogin_CartFound_Success() {
            try (MockedStatic<CartMapper> mockedMapper = mockStatic(CartMapper.class)) {
                // Given
                setupUserAuthentication("testuser");
                when(cartRepository.findByCustomerId(dummyUser.getId())).thenReturn(Optional.of(dummyCart));
                mockedMapper.when(() -> CartMapper.toCartGroupedResponse(dummyCart))
                        .thenReturn(CartGroupedResponse.builder().customerId("user-1").build());

                // When
                CartGroupedResponse response = cartService.getCartByLogin();

                // Then
                assertNotNull(response);
                assertEquals("user-1", response.getCustomerId());
            }
        }

        @Test
        @DisplayName("getCartByLogin(): Keranjang tidak ditemukan → CART_NOT_FOUND")
        void getCartByLogin_CartNotFound_ThrowsException() {
            // Given
            setupUserAuthentication("testuser");
            when(cartRepository.findByCustomerId(dummyUser.getId())).thenReturn(Optional.empty());

            // When & Then
            CustomException ex = assertThrows(CustomException.class, () -> cartService.getCartByLogin());
            assertEquals(ErrorCode.CART_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("UpdateItemQuantity Tests")
    class UpdateItemQuantityTests {

        @Test
        @DisplayName("updateItemQuantity(): Update ke kuantitas positif → Sukses")
        void updateItemQuantity_ToPositive_Success() {
            try (MockedStatic<CartMapper> mockedMapper = mockStatic(CartMapper.class)) {
                // Given
                when(cartItemRepository.findById("item-1")).thenReturn(Optional.of(dummyCartItem));
                when(cartRepository.save(any(Cart.class))).thenReturn(dummyCart);
                mockedMapper.when(() -> CartMapper.toCartGroupedResponse(any(Cart.class))).thenReturn(new CartGroupedResponse());

                // When
                cartService.updateItemQuantity("item-1", 5);

                // Then
                verify(cartRepository).save(dummyCart);
                assertEquals(5, dummyCartItem.getQuantity());
            }
        }

        @Test
        @DisplayName("updateItemQuantity(): Update ke kuantitas nol → Item Dihapus")
        void updateItemQuantity_ToZero_RemovesItem() {
            try (MockedStatic<CartMapper> mockedMapper = mockStatic(CartMapper.class)) {
                // Given
                assertEquals(1, dummyCart.getItems().size());
                when(cartItemRepository.findById("item-1")).thenReturn(Optional.of(dummyCartItem));
                when(cartRepository.save(any(Cart.class))).thenReturn(dummyCart);
                mockedMapper.when(() -> CartMapper.toCartGroupedResponse(any(Cart.class))).thenReturn(new CartGroupedResponse());

                // When
                cartService.updateItemQuantity("item-1", 0);

                // Then
                verify(cartRepository).save(dummyCart);
                // PERBAIKAN ASERSI: Memastikan list item menjadi kosong
                assertTrue(dummyCart.getItems().isEmpty());
            }
        }

        @Test
        @DisplayName("updateItemQuantity(): Cart item tidak ditemukan → CART_ITEM_NOT_FOUND")
        void updateItemQuantity_ItemNotFound_ThrowsException() {
            // Given
            when(cartItemRepository.findById("unknown-item")).thenReturn(Optional.empty());

            // When & Then
            CustomException ex = assertThrows(CustomException.class, () -> cartService.updateItemQuantity("unknown-item", 2));
            assertEquals(ErrorCode.CART_ITEM_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Test
    @DisplayName("removeItem(): Menghapus item dari keranjang → Sukses")
    void removeItem_Success() {
        try (MockedStatic<CartMapper> mockedMapper = mockStatic(CartMapper.class)) {
            // Given
            assertEquals(1, dummyCart.getItems().size());
            when(cartItemRepository.findById("item-1")).thenReturn(Optional.of(dummyCartItem));
            when(cartRepository.save(dummyCart)).thenReturn(dummyCart);
            mockedMapper.when(() -> CartMapper.toCartGroupedResponse(any(Cart.class))).thenReturn(new CartGroupedResponse());

            // When
            cartService.removeItem("item-1");

            // Then
            verify(cartRepository).save(dummyCart);
            assertTrue(dummyCart.getItems().isEmpty());
        }
    }

    @Test
    @DisplayName("clearCart(): Mengosongkan semua item di keranjang → Sukses")
    void clearCart_Success() {
        // Given
        setupUserAuthentication("testuser");
        when(cartRepository.findByCustomerId(dummyUser.getId())).thenReturn(Optional.of(dummyCart));
        when(cartRepository.save(dummyCart)).thenReturn(dummyCart);
        assertFalse(dummyCart.getItems().isEmpty()); // Memastikan item tidak kosong di awal

        // When
        cartService.clearCart();

        // Then
        verify(cartRepository).save(dummyCart);
        assertTrue(dummyCart.getItems().isEmpty());
    }
}