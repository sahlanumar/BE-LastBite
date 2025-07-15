package com.enigma.lastbite.controller;

import com.enigma.lastbite.dto.request.*;
import com.enigma.lastbite.dto.response.*;
import com.enigma.lastbite.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private JwtResponse jwtResponse;
    private TokenRefreshResponse tokenResponse;
    private RegisterResponse registerResponse;

    @BeforeEach
    void setUp() {
        jwtResponse = JwtResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .username("user")
                .email("user@example.com")
                .roles(List.of("ROLE_CUSTOMER"))
                .fullName("User Example")
                .build();

        tokenResponse = TokenRefreshResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .tokenType("Bearer")
                .build();

        registerResponse = RegisterResponse.builder()
                .id("1")
                .username("newuser")
                .fullName("New User")
                .email("newuser@example.com")
                .role("ROLE_CUSTOMER")
                .profileImageUrl("http://image.url")
                .build();
    }

    @Test
    void login_shouldReturnJwtResponse() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .username("user")
                .password("password")
                .build();

        Mockito.when(authService.login(request)).thenReturn(jwtResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("access-token"));
    }

    @Test
    void refreshToken_shouldReturnNewTokens() throws Exception {
        TokenRefreshRequest request = TokenRefreshRequest.builder()
                .refreshToken("old-refresh-token")
                .build();

        Mockito.when(authService.refreshToken(request)).thenReturn(tokenResponse);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
    }

    @Test
    void registerCustomer_shouldReturnRegisterResponse() throws Exception {
        CustomerRegisterRequest request = CustomerRegisterRequest.builder()
                .username("newuser")
                .fullName("New User")
                .email("newuser@example.com")
                .password("password123")
                .phoneNumber("081234567890")
                .latitude(BigDecimal.valueOf(-6.2))
                .longitude(BigDecimal.valueOf(106.8))
                .build();

        Mockito.when(authService.registerCustomer(request)).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register-customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    void registerSeller_shouldReturnRegisterResponse() throws Exception {
        SellerRegisterRequest request = SellerRegisterRequest.builder()
                .username("seller1")
                .fullName("Seller One")
                .email("seller@example.com")
                .password("password123")
                .phoneNumber("081234567890")
                .storeName("Toko Makanan")
                .latitude(BigDecimal.valueOf(-6.2))
                .longitude(BigDecimal.valueOf(106.8))
                .build();

        Mockito.when(authService.registerSeller(request)).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register-seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"));
    }

    @Test
    void registerAdmin_shouldReturnRegisterResponse() throws Exception {
        AdminRegisterRequest request = AdminRegisterRequest.builder()
                .username("admin1")
                .fullName("Admin One")
                .email("admin@example.com")
                .password("adminpass")
                .phoneNumber("081234567890")
                .build();

        Mockito.when(authService.registerAdmin(request)).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("ROLE_CUSTOMER"));
    }

    @Test
    void registerSuperAdmin_shouldReturnRegisterResponse() throws Exception {
        SuperAdminRegisterRequest request = SuperAdminRegisterRequest.builder()
                .username("superadmin")
                .fullName("Super Admin")
                .email("superadmin@example.com")
                .password("superpass")
                .phoneNumber("081234567890")
                .build();

        Mockito.when(authService.registerSuperAdmin(request)).thenReturn(registerResponse);

        mockMvc.perform(post("/api/auth/register-super-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.fullName").value("New User"));
    }
}
