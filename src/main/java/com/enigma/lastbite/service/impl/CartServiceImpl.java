package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartGroupedResponse;
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
import java.util.Optional;

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
    public CartGroupedResponse addItem(AddItemToCartRequest request) {
        String username = getUsernameFromToken();
        User user = userService.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Cart cart = findOrCreateCartForUser(user);
        MenuItem menuItem = menuItemService.findById(request.getMenuItemId());

        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(request.getMenuItemId()))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem itemToUpdate = existingCartItem.get();
            itemToUpdate.setQuantity(itemToUpdate.getQuantity() + request.getQuantity());
        } else {
            CartItem newCartItem = CartMapper.toCartItemEntity(request, menuItem, cart);
            cart.getItems().add(newCartItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return CartMapper.toCartGroupedResponse(updatedCart);
    }

    @Override
    public CartGroupedResponse getCartByLogin() {
        String username = getUsernameFromToken();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        return CartMapper.toCartGroupedResponse(cart);
    }

    @Override
    @Transactional
    public CartGroupedResponse updateItemQuantity(String cartItemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        Cart cart = cartItem.getCart();

        if (quantity <= 0) {
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(quantity);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return CartMapper.toCartGroupedResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartGroupedResponse removeItem(String cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND));

        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return CartMapper.toCartGroupedResponse(updatedCart);
    }

    @Override
    @Transactional
    public void clearCart() {
        String username = getUsernameFromToken();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
        cart.getItems().clear();

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    public Cart findByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId).orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));
    }

    @Override
    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    private String getUsernameFromToken() {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        return jwtUtils.getUsernameFromJwtToken(token);
    }

    private Cart findOrCreateCartForUser(User user) {
        return cartRepository.findByCustomerId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCustomer(user);
            newCart.setItems(new ArrayList<>());
            return cartRepository.save(newCart);
        });
    }
}