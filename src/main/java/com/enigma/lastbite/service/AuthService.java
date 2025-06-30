package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.*;
import com.enigma.lastbite.dto.response.JwtResponse;
import com.enigma.lastbite.dto.response.RegisterResponse;
import com.enigma.lastbite.dto.response.TokenRefreshResponse;
import com.enigma.lastbite.dto.request.AdminRegisterRequest;
import com.enigma.lastbite.dto.request.CustomerRegisterRequest;
import com.enigma.lastbite.dto.request.SellerRegisterRequest;


/**
 * Service untuk mengelola autentikasi
 */
public interface AuthService {

    JwtResponse login(LoginRequest loginRequest);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    RegisterResponse registerCustomer(CustomerRegisterRequest customerRegisterRequest);
    RegisterResponse registerAdmin(AdminRegisterRequest adminRegisterRequest);
    RegisterResponse registerSeller(SellerRegisterRequest sellerRegisterRequest);
}
