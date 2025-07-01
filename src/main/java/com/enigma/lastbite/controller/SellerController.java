package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.UpdateSellerRequest;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.PagingResponse;
import com.enigma.lastbite.dto.response.SellerResponse;
import com.enigma.lastbite.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;


    @GetMapping
    public ResponseEntity<CommonResponse<List<SellerResponse>>> getAllSellers(
            @RequestParam(required = false) String storeName,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "storeName") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<SellerResponse> sellerPage = sellerService.getAll(storeName, status, page, size, sortField, sortDir);

        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page + 1)
                .totalPage(sellerPage.getTotalPages())
                .size(size)
                .build();

        CommonResponse<List<SellerResponse>> response = CommonResponse.<List<SellerResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully fetched sellers.")
                .data(sellerPage.getContent())
                .paging(pagingResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<SellerResponse>> getSellerById(@PathVariable String id) {
        SellerResponse sellerResponse = sellerService.getById(id);
        CommonResponse<SellerResponse> response = CommonResponse.<SellerResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully fetched seller.")
                .data(sellerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResponse<SellerResponse>> getSellerByUserId(@PathVariable String userId) {
        SellerResponse sellerResponse = sellerService.getByUserId(userId);
        CommonResponse<SellerResponse> response = CommonResponse.<SellerResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully fetched seller by user id.")
                .data(sellerResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<SellerResponse>> updateSeller(@PathVariable String id, @RequestBody UpdateSellerRequest request) {
        SellerResponse sellerResponse = sellerService.update(id, request);
        CommonResponse<SellerResponse> response = CommonResponse.<SellerResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Successfully updated seller.")
                .data(sellerResponse)
                .build();
        return ResponseEntity.ok(response);
    }


}