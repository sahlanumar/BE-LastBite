package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartResponse;
import com.enigma.lastbite.entity.Cart;
import com.enigma.lastbite.entity.CartItem;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.CartMapper;
import com.enigma.lastbite.repository.CartItemRepository;
import com.enigma.lastbite.repository.CartRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.CartService;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final JwtUtils jwtUtils;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final MenuItemService menuItemService;


    @Override
    public CartResponse addItem(AddItemToCartRequest request) {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User user = userService.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByCustomerId(user.getId()).orElse(null);

        if(cart == null) {
            cart = new Cart();
            cart.setCustomer(user);
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);
        }

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setMenuItem(menuItemService.findById(request.getMenuItemId()));
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return CartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse getCartByLogin() {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        return CartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse updateItemQuantity(String cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (quantity <= 0) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return CartMapper.toCartResponse(cartItem.getCart());
    }

    @Override
    public CartResponse removeItem(String cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);

        return CartMapper.toCartResponse(cart);
    }

    @Override
    public void clearCart() {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        cartItemRepository.deleteAll(cart.getItems());
    }
}
