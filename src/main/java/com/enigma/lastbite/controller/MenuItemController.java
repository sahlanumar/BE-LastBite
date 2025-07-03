package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.ResponseMessage;
import com.enigma.lastbite.dto.request.MenuItemCreateRequest;
import com.enigma.lastbite.dto.request.MenuItemUpdateRequest;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.MenuItemResponse;
import com.enigma.lastbite.service.MenuItemService;
import com.enigma.lastbite.util.ResponseUtil;
import com.enigma.lastbite.dto.request.MenuItemSearchRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<CommonResponse<MenuItemResponse>> createMenuItem(
            @RequestBody MenuItemCreateRequest request) {

        MenuItemResponse response = menuItemService.create(request);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_SAVE_DATA, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<MenuItemResponse>> getMenuItemById(@PathVariable String id) {
        MenuItemResponse response = menuItemService.getById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, response);
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<MenuItemResponse>>> getAllMenuItems(
            // PERBAIKAN: Gunakan @ModelAttribute untuk menangkap semua parameter filter ke dalam satu objek
            @ModelAttribute MenuItemSearchRequest request,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            HttpServletRequest httpServletRequest // Gunakan ini untuk mendapatkan URL
    ) {
        // Panggil service dengan getter dari objek 'request'
        Page<MenuItemResponse> menuPage = menuItemService.getAll(
                request.getName(), request.getSellerId(), request.getMaxPrice(),
                request.getMinPrice(), request.getIsAvailable(), request.getStatus(),
                request.getMinRating(), request.getMaxRating(),
                page, size, sortField, sortDir,
                request.getLat(), request.getLon()
        );

        // Kirim objek 'request' sebagai filter agar link paginasi benar
        return ResponseUtil.buildResponse(
                HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA,
                menuPage.getContent(),
                menuPage,
                httpServletRequest.getRequestURI(), // URL dinamis dan robust
                request, // Ini akan memastikan link paginasi menyertakan semua filter
                sortField,
                sortDir
        );
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<List<MenuItemResponse>>> getMyMenuItems(
            @ModelAttribute MenuItemSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            HttpServletRequest httpServletRequest) {

        // --- PERBAIKAN: Tambahkan minRating dan maxRating saat memanggil service ---
        Page<MenuItemResponse> menuPage = menuItemService.getAllByLogin(
                request.getName(), request.getMaxPrice(), request.getMinPrice(),
                request.getIsAvailable(), request.getStatus(),
                request.getMinRating(),
                request.getMaxRating(),
                page, size, sortField, sortDir);

        return ResponseUtil.buildResponse(
                HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA,
                menuPage.getContent(),
                menuPage,
                httpServletRequest.getRequestURI(),
                request,
                sortField,
                sortDir
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<MenuItemResponse>> updateMenuItem(
            @PathVariable String id,
            @RequestBody MenuItemUpdateRequest request) {

        MenuItemResponse response = menuItemService.update(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_UPDATE_DATA, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteMenuItem(@PathVariable String id) {
        menuItemService.deleteById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_DELETE_DATA, null);
    }
}
