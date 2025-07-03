package com.enigma.lastbite.controller;

import com.enigma.lastbite.constant.ResponseMessage;
import com.enigma.lastbite.dto.request.*;
import com.enigma.lastbite.dto.response.CommonResponse;
import com.enigma.lastbite.dto.response.JwtResponse;
import com.enigma.lastbite.dto.response.RegisterResponse;
import com.enigma.lastbite.dto.response.TokenRefreshResponse;
import com.enigma.lastbite.service.AuthService;
import com.enigma.lastbite.util.ResponseUtil;
import com.enigma.lastbite.validation.ValidationGroups;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        JwtResponse jwt = authService.login(loginRequest);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_LOGIN, jwt);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<CommonResponse<TokenRefreshResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {

        TokenRefreshResponse token = authService.refreshToken(request);
        return ResponseUtil.buildResponse(HttpStatus.OK, ResponseMessage.SUCCESS_REFRESH_TOKEN, token);
    }

    @PostMapping("/register-customer")
    public ResponseEntity<CommonResponse<RegisterResponse>> registerCustomer(
            @Validated(ValidationGroups.Create.class) @RequestBody CustomerRegisterRequest req) {

        RegisterResponse res = authService.registerCustomer(req);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_REGISTER, res);
    }

    @PostMapping("/register-seller")
    public ResponseEntity<CommonResponse<RegisterResponse>> registerSeller(
            @Validated(ValidationGroups.Create.class) @RequestBody SellerRegisterRequest req) {

        RegisterResponse res = authService.registerSeller(req);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_REGISTER, res);
    }

    @PostMapping("/register-admin")
    public ResponseEntity<CommonResponse<RegisterResponse>> registerAdmin(
            @Validated(ValidationGroups.Create.class) @RequestBody AdminRegisterRequest req) {

        RegisterResponse res = authService.registerAdmin(req);
        return ResponseUtil.buildResponse(HttpStatus.CREATED, ResponseMessage.SUCCESS_REGISTER, res);
    }
}