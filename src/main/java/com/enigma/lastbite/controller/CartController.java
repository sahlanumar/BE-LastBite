package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.ResponseMessage;
import com.enigma.lastbite.dto.request.AddItemToCartRequest;
import com.enigma.lastbite.dto.response.CartGroupedResponse;
import com.enigma.lastbite.dto.response.CartResponse;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.service.CartService;
import com.enigma.lastbite.util.ResponseUtil;
import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Validated // Anotasi ini diperlukan untuk mengaktifkan validasi pada parameter method
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CommonResponse<CartResponse>> addItem(
            @Validated(ValidationGroups.Create.class) @RequestBody AddItemToCartRequest request) {

        CartResponse cart = cartService.addItem(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_SAVE_DATA, cart);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<CartGroupedResponse>> getCartByLogin() {
        CartGroupedResponse cart = cartService.getCartByLogin();
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, cart);
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CommonResponse<CartResponse>> updateItemQuantity(
            @PathVariable String cartItemId,
            @RequestParam @Min(value = 1, message = "Kuantitas minimal harus 1") Integer quantity) {

        CartResponse cart = cartService.updateItemQuantity(cartItemId, quantity);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_UPDATE_DATA, cart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CommonResponse<CartResponse>> removeItem(
            @PathVariable String cartItemId) {

        CartResponse cart = cartService.removeItem(cartItemId);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_DELETE_DATA, cart);
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> clearCart() {
        cartService.clearCart();
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_DELETE_DATA, null);
    }
}