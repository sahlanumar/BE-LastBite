package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.ResponseMessage;
import com.enigma.lastbite.dto.request.MenuItemReviewCreateRequest;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.MenuItemReviewResponse;
import com.enigma.lastbite.dto.response.UnreviewedItemResponse;
import com.enigma.lastbite.service.MenuItemReviewService;
import com.enigma.lastbite.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-item-reviews")
@RequiredArgsConstructor
public class MenuItemReviewController {

    private final MenuItemReviewService menuItemReviewService;

    @PostMapping
    public ResponseEntity<CommonResponse<MenuItemReviewResponse>> createReview(
            @Validated @RequestBody MenuItemReviewCreateRequest request) {

        MenuItemReviewResponse review = menuItemReviewService.createReview(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_SAVE_DATA, review);
    }

    @GetMapping("/to-review")
    public ResponseEntity<CommonResponse<List<UnreviewedItemResponse>>> getItemsToReview() {
        List<UnreviewedItemResponse> unreviewedItems = menuItemReviewService.getUnreviewedItemsForCustomer();
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, unreviewedItems);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteReview(@PathVariable String id) {
        menuItemReviewService.deleteReview(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_DELETE_DATA, null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<MenuItemReviewResponse>> getReviewById(@PathVariable String id) {
        MenuItemReviewResponse review = menuItemReviewService.getReviewById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, review);
    }

    @GetMapping("/menu/{menuItemId}")
    public ResponseEntity<CommonResponse<List<MenuItemReviewResponse>>> getReviewsByMenuItemId(
            @PathVariable String menuItemId) {

        List<MenuItemReviewResponse> reviews = menuItemReviewService.getReviewsByMenuItemId(menuItemId);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, reviews);
    }
}