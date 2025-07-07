package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartGroupedResponse;
import com.enigma.lastbite.dto.response.CartResponse;
import com.enigma.lastbite.entity.Cart;

public interface CartService {
    CartGroupedResponse addItem( AddItemToCartRequest request);
    CartGroupedResponse getCartByLogin();
    CartGroupedResponse updateItemQuantity( String cartItemId, Integer quantity);
    CartGroupedResponse removeItem( String cartItemId);
    void clearCart();
    Cart findByCustomerId(String customerId);
    Cart save(Cart cart);

}
