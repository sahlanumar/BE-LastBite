package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartResponse;

public interface CartService {
    CartResponse addItem( AddItemToCartRequest request);
    CartResponse getCartByLogin();
    CartResponse updateItemQuantity( String cartItemId, Integer quantity);
    CartResponse removeItem( String cartItemId);
    void clearCart();
}
