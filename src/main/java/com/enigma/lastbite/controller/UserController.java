package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.ResponseMessage;
import com.enigma.lastbite.dto.request.PasswordChangeRequest;
import com.enigma.lastbite.dto.request.UserUpdateRequest;
import com.enigma.lastbite.dto.request.UserFilterRequest;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.UserResponse;
import com.enigma.lastbite.service.UserService;
import com.enigma.lastbite.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<CommonResponse<List<UserResponse>>>getAllUsers(
            @ModelAttribute UserFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String baseUrl
    ) {
        Page<UserResponse> userPage = userService.findAllUsers(filter, page, size, sortField, sortDir);
        return ResponseUtil.buildResponse(
                HttpStatus.OK,
                ResponseMessage.SUCCESS_GET_DATA,
                userPage.getContent(),
                userPage,
                baseUrl,
                filter,
                sortField,
                sortDir
        );
    }

    @GetMapping("/me")
    public ResponseEntity<CommonResponse<UserResponse>> getMe() {
        UserResponse response = userService.getUserByLogin();
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_GET_DATA, response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> updateUserById(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUserById(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_UPDATE_DATA, response);
    }

    @PutMapping("/me")
    public ResponseEntity<CommonResponse<UserResponse>> updateUser(@RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(request);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_UPDATE_DATA, response);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<CommonResponse<UserResponse>> changePasswordById(@PathVariable String id, @RequestBody PasswordChangeRequest request) {
        UserResponse response = userService.updatePasswordById(id, request);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_UPDATE_DATA, response);
    }

    @PutMapping("/me/password")
    public ResponseEntity<CommonResponse<UserResponse>> changePassword(@RequestBody PasswordChangeRequest request) {
        UserResponse response = userService.updatePassword(request);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_UPDATE_DATA, response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<UserResponse>> deleteUser(@PathVariable String id) {
        userService.deleteUserById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_DELETE_DATA, null);
    }
}
