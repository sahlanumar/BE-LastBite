package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartResponse;
import com.enigma.lastbite.entity.Cart;
import com.enigma.lastbite.entity.CartItem;
import com.enigma.lastbite.entity.MenuItem;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final JwtUtils jwtUtils;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final MenuItemService menuItemService;

    @Override
    @Transactional
    public CartResponse addItem(AddItemToCartRequest request) {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User user = userService.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByCustomerId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCustomer(user);
            newCart.setCreatedAt(LocalDateTime.now());
            newCart.setUpdatedAt(LocalDateTime.now());
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });

        MenuItem menuItem = menuItemService.findById(request.getMenuItemId());

        CartItem cartItem = CartMapper.toCartItemEntity(request, menuItem, cart);

        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cart.getItems().add(cartItem);
        Cart updatedCart = cartRepository.save(cart);


        return CartMapper.toCartResponse(updatedCart);
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
    @Transactional
    public CartResponse updateItemQuantity(String cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return CartMapper.toCartResponse(cartItem.getCart());
        }

        cartItem.setQuantity(quantity);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        Cart cart = updatedCartItem.getCart();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return CartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeItem(String cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        Cart cart = cartItem.getCart();
        cartItemRepository.delete(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return CartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        cartItemRepository.deleteAll(cart.getItems());

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}