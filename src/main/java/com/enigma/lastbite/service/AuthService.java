package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.*;
import com.enigma.lastbite.dto.response.JwtResponse;
import com.enigma.lastbite.dto.response.LoginResponse;
import com.enigma.lastbite.dto.response.RegisterResponse;
import com.enigma.lastbite.dto.response.TokenRefreshResponse;
import com.enigma.lastbite.dto.request.RegisterAdminRequest;
import com.enigma.lastbite.dto.request.RegisterCustomerRequest;
import com.enigma.lastbite.dto.request.RegisterSellerRequest;


/**
 * Service untuk mengelola autentikasi
 */
public interface AuthService {

    JwtResponse login(LoginRequest loginRequest);
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
    RegisterResponse registerCustomer(RegisterCustomerRequest registerCustomerRequest);
    RegisterResponse registerAdmin(RegisterAdminRequest registerAdminRequest);
    RegisterResponse registerSeller(RegisterSellerRequest registerSellerRequest);
}
