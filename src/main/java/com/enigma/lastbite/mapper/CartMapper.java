package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.ListingStatus;
import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartGroupedResponse;
import com.enigma.lastbite.dto.response.CartItemResponse;
import com.enigma.lastbite.dto.response.SellerCartResponse;
import com.enigma.lastbite.entity.Cart;
import com.enigma.lastbite.entity.CartItem;
import com.enigma.lastbite.entity.MenuItem;
import com.enigma.lastbite.entity.SellerProfile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartItem toCartItemEntity(AddItemToCartRequest request, MenuItem menuItem, Cart cart) {
        return CartItem.builder()
                .cart(cart)
                .menuItem(menuItem)
                .quantity(request.getQuantity())
                .build();
    }

    /**
     * Mengubah entitas CartItem menjadi DTO CartItemResponse.
     * Termasuk logika untuk menentukan 'status' item secara dinamis.
     */
    public static CartItemResponse toCartItemResponse(CartItem cartItem) {
        String dynamicStatus = determineItemStatus(cartItem);

        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .menuItemId(cartItem.getMenuItem().getId())
                .menuItemName(cartItem.getMenuItem().getName())
                .imageUrl(cartItem.getMenuItem().getImageUrl())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getMenuItem().getDiscountedPrice())
                .subtotal(cartItem.getMenuItem().getDiscountedPrice().multiply(new BigDecimal(cartItem.getQuantity())))
                .status(dynamicStatus)
                .build();
    }

    private static String determineItemStatus(CartItem cartItem) {
        MenuItem menuItem = cartItem.getMenuItem();
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(menuItem.getDisplayEndTime()) || now.isBefore(menuItem.getDisplayStartTime())) {
            return ListingStatus.NOT_AVAILABLE.name();
        }

        if (menuItem.getQuantityAvailable() < cartItem.getQuantity()) {
            return ListingStatus.SOLD_OUT.name();
        }

        return ListingStatus.AVAILABLE.name();
    }

    public static CartGroupedResponse toCartGroupedResponse(Cart cart) {
        Map<SellerProfile, List<CartItem>> itemsBySeller = cart.getItems().stream()
                .collect(Collectors.groupingBy(cartItem -> cartItem.getMenuItem().getSellerProfile()));

        List<SellerCartResponse> sellerCarts = itemsBySeller.entrySet().stream()
                .map(entry -> {
                    SellerProfile seller = entry.getKey();
                    List<CartItem> sellerItems = entry.getValue();

                    List<CartItemResponse> itemResponses = sellerItems.stream()
                            .map(CartMapper::toCartItemResponse) // Ini akan otomatis memanggil mapper yang sudah ada logikanya
                            .collect(Collectors.toList());

                    BigDecimal sellerSubtotal = itemResponses.stream()
                            .map(CartItemResponse::getSubtotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return SellerCartResponse.builder()
                            .sellerId(seller.getId())
                            .storeName(seller.getStoreName())
                            .storeImageUrl(seller.getStoreImageUrl())
                            .items(itemResponses)
                            .sellerSubtotal(sellerSubtotal)
                            .build();
                })
                .collect(Collectors.toList());

        BigDecimal grandTotal = sellerCarts.stream()
                .map(SellerCartResponse::getSellerSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartGroupedResponse.builder()
                .cartId(cart.getId())
                .customerId(cart.getCustomer().getId())
                .sellers(sellerCarts)
                .grandTotal(grandTotal)
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}