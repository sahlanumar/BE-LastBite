package com.enigma.lastbite.mapper;

import com.enigma.lastbite.dto.response.CartItemResponse;
import com.enigma.lastbite.dto.response.CartResponse;
import com.enigma.lastbite.entity.Cart;
import com.enigma.lastbite.entity.CartItem;

public class CartMapper {
    public static CartItemResponse toCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .cartItemId(cartItem.getId())
                .menuItemId(cartItem.getMenuItem().getId().toString())
                .menuItemName(cartItem.getMenuItem().getName())
                .storeName(cartItem.getMenuItem().getSellerProfile().getStoreName())
                .imageUrl(cartItem.getMenuItem().getImageUrl())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getMenuItem().getDiscountedPrice())
                .subtotal(cartItem.getMenuItem().getDiscountedPrice().multiply(new java.math.BigDecimal(cartItem.getQuantity())))
                .build();
    }

    public static CartResponse toCartResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .customerId(cart.getCustomer().getId().toString())
                .items(cart.getItems().stream().map(CartMapper::toCartItemResponse).toList())
                .totalPrice(cart.getItems().stream().map(cartItem -> cartItem.getMenuItem().getDiscountedPrice().multiply(new java.math.BigDecimal(cartItem.getQuantity()))).reduce(new java.math.BigDecimal(0), java.math.BigDecimal::add))
                .updatedAt(cart.getUpdatedAt())
                .build();

    }
}
