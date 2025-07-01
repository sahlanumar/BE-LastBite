package com.enigma.lastbite.controller;

import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartResponse;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CommonResponse<CartResponse>> addItem(
            @RequestBody AddItemToCartRequest request) {

        CartResponse cart = cartService.addItem(request);

        CommonResponse<CartResponse> response = CommonResponse.<CartResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Successfully added item to cart.")
                .data(cart)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<CartResponse>> getCartByLogin() {
        CartResponse cart = cartService.getCartByLogin();

        CommonResponse<CartResponse> response = CommonResponse.<CartResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully fetched cart.")
                .data(cart)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CommonResponse<CartResponse>> updateItemQuantity(
            @PathVariable String cartItemId,
            @RequestParam Integer quantity) {

        CartResponse cart = cartService.updateItemQuantity(cartItemId, quantity);

        CommonResponse<CartResponse> response = CommonResponse.<CartResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated item quantity.")
                .data(cart)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CommonResponse<CartResponse>> removeItem(
            @PathVariable String cartItemId) {

        CartResponse cart = cartService.removeItem(cartItemId);

        CommonResponse<CartResponse> response = CommonResponse.<CartResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully removed item from cart.")
                .data(cart)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> clearCart() {
        cartService.clearCart();

        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully cleared cart.")
                .build();

        return ResponseEntity.ok(response);
    }
}
