package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.ResponseMessage;
import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.SellerFilterRequest;
import com.enigma.lastbite.dto.request.SellerUpdateRequest;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.PagingResponse;
import com.enigma.lastbite.dto.response.SellerResponse;
import com.enigma.lastbite.service.SellerService;
import com.enigma.lastbite.util.ResponseUtil;
import com.enigma.lastbite.validation.ValidationGroups;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String baseUrl
    ) {
        Page<SellerResponse> sellerPage = sellerService.getAll(storeName, status, page, size, sortField, sortDir);

        SellerFilterRequest filter = new SellerFilterRequest();
        filter.setStoreName(storeName);
        filter.setStatus(status);

        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ResponseMessage.SUCCESS_GET_DATA,
                sellerPage.getContent(),
                sellerPage,
                baseUrl,
                filter,
                sortField,
                sortDir
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<SellerResponse>> getSellerById(@PathVariable String id) {
        SellerResponse sellerResponse = sellerService.getById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, sellerResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<SellerResponse>> getSellerByAuth() {
        SellerResponse sellerResponse = sellerService.getByLogin();
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, sellerResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CommonResponse<SellerResponse>> getSellerByUserId(@PathVariable String userId) {
        SellerResponse sellerResponse = sellerService.getByUserId(userId);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, sellerResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<SellerResponse>> updateSeller(
            @PathVariable String id,
            @Validated(ValidationGroups.Update.class) @RequestBody SellerUpdateRequest request) {
        SellerResponse sellerResponse = sellerService.update(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_UPDATE_DATA, sellerResponse);
    }
}