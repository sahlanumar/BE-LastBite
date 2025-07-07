package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartGroupedResponse;
import com.enigma.lastbite.dto.response.CartResponse;
import com.enigma.lastbite.entity.Cart;

public interface CartService {
    CartResponse addItem( AddItemToCartRequest request);
    CartGroupedResponse getCartByLogin();
    CartResponse updateItemQuantity( String cartItemId, Integer quantity);
    CartResponse removeItem( String cartItemId);
    void clearCart();
    Cart findByCustomerId(String customerId);
    Cart save(Cart cart);

}
