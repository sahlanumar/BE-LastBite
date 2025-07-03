package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.ResponseMessage;
import com.enigma.lastbite.dto.request.MenuItemReviewCreateRequest;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.MenuItemReviewResponse;
import com.enigma.lastbite.service.MenuItemReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-item-reviews")
@RequiredArgsConstructor
public class MenuItemReviewController {

    private final MenuItemReviewService menuItemReviewService;

    @PostMapping
    public ResponseEntity<CommonResponse<MenuItemReviewResponse>> createReview(
            @RequestBody MenuItemReviewCreateRequest request) {

        MenuItemReviewResponse review = menuItemReviewService.createReview(request);

        CommonResponse<MenuItemReviewResponse> response = CommonResponse.<MenuItemReviewResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(ResponseMessage.SUCCESS_SAVE_DATA)
                .data(review)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteReview(@PathVariable String id) {
        menuItemReviewService.deleteReview(id);

        CommonResponse<Void> response = CommonResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_DELETE_DATA)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<MenuItemReviewResponse>> getReviewById(@PathVariable String id) {
        MenuItemReviewResponse review = menuItemReviewService.getReviewById(id);

        CommonResponse<MenuItemReviewResponse> response = CommonResponse.<MenuItemReviewResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(review)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/menu/{menuItemId}")
    public ResponseEntity<CommonResponse<List<MenuItemReviewResponse>>> getReviewsByMenuItemId(
            @PathVariable String menuItemId) {

        List<MenuItemReviewResponse> reviews = menuItemReviewService.getReviewsByMenuItemId(menuItemId);

        CommonResponse<List<MenuItemReviewResponse>> response = CommonResponse.<List<MenuItemReviewResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(ResponseMessage.SUCCESS_GET_DATA)
                .data(reviews)
                .build();

        return ResponseEntity.ok(response);
    }
}
