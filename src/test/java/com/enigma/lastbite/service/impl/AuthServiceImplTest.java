package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.*;
import com.enigma.lastbite.dto.response.JwtResponse;
import com.enigma.lastbite.dto.response.RegisterResponse;
import com.enigma.lastbite.dto.response.TokenRefreshResponse;
import com.enigma.lastbite.entity.Role;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.AuthMapper;
import com.enigma.lastbite.repository.SellerProfileRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.RoleService;
import com.enigma.lastbite.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;
    @Mock private UserService userService;
    @Mock private RoleService roleService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private SellerProfileRepository sellerProfileRepository;
    @Mock private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @InjectMocks private AuthServiceImpl authService;

    private User dummyUser;
    private Role dummyRole;

    @BeforeEach
    void setUp() {
        dummyRole = Role.builder().id("role1").name(UserRole.ROLE_CUSTOMER).build();
        dummyUser = User.builder()
                .id("user1")
                .username("testuser")
                .email("test@example.com")
                .fullName("Test User")
                .phoneNumber("081234567890")
                .passwordHash("hashedPassword")
                .roles(Set.of(dummyRole))
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("login(): dengan username valid → sukses")
        void login_withUsername_success() {
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
            // Given
            LoginRequest request = LoginRequest.builder().username("testuser").password("password").build();
            Authentication auth = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testuser");
            doReturn(authorities).when(userDetails).getAuthorities();
            when(userService.findByUsername("testuser")).thenReturn(Optional.of(dummyUser));
            when(jwtUtils.generateJwtToken(auth)).thenReturn("access_token");
            when(jwtUtils.generateRefreshToken("testuser")).thenReturn("refresh_token");
            when(sellerProfileRepository.findByUserId(dummyUser.getId())).thenReturn(Optional.empty());

            // Mock SecurityContextHolder
            SecurityContext securityContext = mock(SecurityContext.class);
            SecurityContextHolder.setContext(securityContext);
            doNothing().when(securityContext).setAuthentication(auth);

            // When
            JwtResponse response = authService.login(request);

            // Then
            assertNotNull(response);
            assertEquals("access_token", response.getToken());
            assertEquals("refresh_token", response.getRefreshToken());
            assertEquals("testuser", response.getUsername());
            assertEquals(UserStatus.ACTIVE, response.getStatus());
            assertTrue(response.getRoles().contains("ROLE_CUSTOMER"));
            verify(authenticationManager).authenticate(any());
        }

        @Test
        @DisplayName("login(): dengan email valid → sukses")
        void login_withEmail_success() {
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
            // Given
            LoginRequest request = LoginRequest.builder().email("test@example.com").password("password").build();
            Authentication auth = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testuser");
            doReturn(authorities).when(userDetails).getAuthorities();
            when(userService.findByUsername("testuser")).thenReturn(Optional.of(dummyUser));
            when(jwtUtils.generateJwtToken(auth)).thenReturn("access_token");
            when(jwtUtils.generateRefreshToken("testuser")).thenReturn("refresh_token");
            when(sellerProfileRepository.findByUserId(dummyUser.getId())).thenReturn(Optional.empty());

            // Mock SecurityContextHolder
            SecurityContext securityContext = mock(SecurityContext.class);
            SecurityContextHolder.setContext(securityContext);
            doNothing().when(securityContext).setAuthentication(auth);

            // When
            JwtResponse response = authService.login(request);

            // Then
            assertNotNull(response);
            assertEquals("test@example.com", response.getEmail());
            verify(authenticationManager).authenticate(argThat(token -> token.getPrincipal().equals("test@example.com")));
        }

        @Test
        @DisplayName("login(): sebagai seller dengan status PENDING → sukses")
        void login_asSeller_success() {
            // Given
            LoginRequest request = LoginRequest.builder().username("selleruser").password("password").build();
            Authentication auth = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);
            SellerProfile sellerProfile = SellerProfile.builder().id("sp1").status(UserStatus.INACTIVE).user(dummyUser).build();

            when(authenticationManager.authenticate(any())).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("selleruser");
            when(userService.findByUsername("selleruser")).thenReturn(Optional.of(dummyUser));
            when(sellerProfileRepository.findByUserId(dummyUser.getId())).thenReturn(Optional.of(sellerProfile));

            // When
            JwtResponse response = authService.login(request);

            // Then
            assertEquals(UserStatus.INACTIVE, response.getStatus());
        }

        @Test
        @DisplayName("login(): tanpa username atau email → USERNAME_OR_EMAIL_REQUIRED")
        void login_noUsernameOrEmail_throwsException() {
            LoginRequest request = LoginRequest.builder().password("password").build();

            CustomException ex = assertThrows(CustomException.class, () -> authService.login(request));
            assertEquals(ErrorCode.USERNAME_OR_EMAIL_REQUIRED, ex.getErrorCode());
        }

        @Test
        @DisplayName("login(): tanpa password → PASSWORD_REQUIRED")
        void login_noPassword_throwsException() {
            LoginRequest request = LoginRequest.builder().username("testuser").build();

            CustomException ex = assertThrows(CustomException.class, () -> authService.login(request));
            assertEquals(ErrorCode.PASSWORD_REQUIRED, ex.getErrorCode());
        }

        @Test
        @DisplayName("login(): kredensial salah → BadCredentialsException")
        void login_badCredentials_throwsException() {
            LoginRequest request = LoginRequest.builder().username("testuser").password("wrongpassword").build();
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

            assertThrows(BadCredentialsException.class, () -> authService.login(request));
        }

        @Test
        @DisplayName("login(): user tidak ditemukan setelah otentikasi → USER_NOT_FOUND")
        void login_userNotFoundAfterAuth_throwsException() {
            // Given
            LoginRequest request = LoginRequest.builder().username("testuser").password("password").build();
            Authentication auth = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(auth.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("testuser");
            when(userService.findByUsername("testuser")).thenReturn(Optional.empty()); // User not found

            SecurityContext securityContext = mock(SecurityContext.class);
            SecurityContextHolder.setContext(securityContext);
            doNothing().when(securityContext).setAuthentication(auth);

            // When & Then
            CustomException ex = assertThrows(CustomException.class, () -> authService.login(request));
            assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("RefreshToken Tests")
    class RefreshTokenTests {

        @Test
        @DisplayName("refreshToken(): token valid → sukses")
        void refreshToken_success() {
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
            // Given
            TokenRefreshRequest request = TokenRefreshRequest.builder().refreshToken("valid_refresh_token").build();
            UserDetails userDetails = mock(UserDetails.class);

            when(jwtUtils.validateJwtToken("valid_refresh_token")).thenReturn(true);
            when(jwtUtils.isRefreshToken("valid_refresh_token")).thenReturn(true);
            when(jwtUtils.getUsernameFromJwtToken("valid_refresh_token")).thenReturn("testuser");
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
            doReturn(authorities).when(userDetails).getAuthorities();
            when(jwtUtils.generateJwtToken("testuser", List.of("ROLE_CUSTOMER"))).thenReturn("new_access_token");

            // When
            TokenRefreshResponse response = authService.refreshToken(request);

            // Then
            assertNotNull(response);
            assertEquals("new_access_token", response.getAccessToken());
            assertEquals("valid_refresh_token", response.getRefreshToken());
            assertEquals("Bearer", response.getTokenType());
        }

        @Test
        @DisplayName("refreshToken(): token tidak valid → TOKEN_NOT_VALID")
        void refreshToken_invalidToken_throwsException() {
            TokenRefreshRequest request = TokenRefreshRequest.builder().refreshToken("invalid_token").build();
            when(jwtUtils.validateJwtToken("invalid_token")).thenReturn(false);

            CustomException ex = assertThrows(CustomException.class, () -> authService.refreshToken(request));
            assertEquals(ErrorCode.TOKEN_NOT_VALID, ex.getErrorCode());
        }

        @Test
        @DisplayName("refreshToken(): token bukan refresh token → NOT_REFRESH_TOKEN")
        void refreshToken_notARefreshToken_throwsException() {
            TokenRefreshRequest request = TokenRefreshRequest.builder().refreshToken("not_a_refresh_token").build();
            when(jwtUtils.validateJwtToken("not_a_refresh_token")).thenReturn(true);
            when(jwtUtils.isRefreshToken("not_a_refresh_token")).thenReturn(false);

            CustomException ex = assertThrows(CustomException.class, () -> authService.refreshToken(request));
            assertEquals(ErrorCode.NOT_REFRESH_TOKEN, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("registerCustomer(): request valid → sukses")
        void registerCustomer_success() {
            try (MockedStatic<AuthMapper> mockedMapper = mockStatic(AuthMapper.class)) {
                // Given
                CustomerRegisterRequest request = new CustomerRegisterRequest();
                when(userService.existsByEmail(any())).thenReturn(false);
                when(userService.existsByUsername(any())).thenReturn(false);
                when(userService.existsByPhoneNumber(any())).thenReturn(false);
                when(roleService.getOrCreate(UserRole.ROLE_CUSTOMER)).thenReturn(dummyRole);
                when(userService.save(any(User.class))).thenReturn(dummyUser);

                mockedMapper.when(() -> AuthMapper.toUser(any(CustomerRegisterRequest.class), any(), any())).thenReturn(dummyUser);
                mockedMapper.when(() -> AuthMapper.toRegisterResponse(dummyUser)).thenReturn(RegisterResponse.builder().username("testuser").build());

                // When
                RegisterResponse response = authService.registerCustomer(request);

                // Then
                assertNotNull(response);
                assertEquals("testuser", response.getUsername());
                verify(userService).save(dummyUser);
                verify(roleService).getOrCreate(UserRole.ROLE_CUSTOMER);
            }
        }

        @Test
        @DisplayName("registerCustomer(): email sudah ada → EMAIL_ALREADY_EXISTS")
        void registerCustomer_emailExists_throwsException() {
            CustomerRegisterRequest request = CustomerRegisterRequest.builder().email("exists@example.com").build();
            when(userService.existsByEmail("exists@example.com")).thenReturn(true);

            CustomException ex = assertThrows(CustomException.class, () -> authService.registerCustomer(request));
            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, ex.getErrorCode());
        }

        @Test
        @DisplayName("registerSeller(): request valid → sukses")
        void registerSeller_success() {
            try (MockedStatic<AuthMapper> mockedMapper = mockStatic(AuthMapper.class)) {
                // Given
                SellerRegisterRequest request = new SellerRegisterRequest();
                SellerProfile sellerProfile = new SellerProfile();

                when(userService.existsByEmail(any())).thenReturn(false);
                when(userService.existsByUsername(any())).thenReturn(false);
                when(userService.existsByPhoneNumber(any())).thenReturn(false);
                when(roleService.getOrCreate(UserRole.ROLE_SELLER)).thenReturn(new Role(null, UserRole.ROLE_SELLER));
                when(userService.save(any(User.class))).thenReturn(dummyUser);
                when(sellerProfileRepository.save(any(SellerProfile.class))).thenReturn(sellerProfile);

                mockedMapper.when(() -> AuthMapper.toUser(any(SellerRegisterRequest.class), any(), any())).thenReturn(dummyUser);
                mockedMapper.when(() -> AuthMapper.toSellerProfile(request, dummyUser)).thenReturn(sellerProfile);
                mockedMapper.when(() -> AuthMapper.toRegisterResponse(dummyUser)).thenReturn(RegisterResponse.builder().username("selleruser").build());

                // When
                RegisterResponse response = authService.registerSeller(request);

                // Then
                assertNotNull(response);
                assertEquals("selleruser", response.getUsername());
                verify(userService).save(dummyUser);
                verify(sellerProfileRepository).save(sellerProfile);
            }
        }

        @Test
        @DisplayName("registerSuperAdmin(): request valid dengan secret key benar → sukses")
        void registerSuperAdmin_success() {
            try (MockedStatic<AuthMapper> mockedMapper = mockStatic(AuthMapper.class)) {
                // Given
                String secretKey = "super-secret";
                ReflectionTestUtils.setField(authService, "actualSecretKey", secretKey); // Set private field

                SuperAdminRegisterRequest request = SuperAdminRegisterRequest.builder().secretKeySuperAdmin(secretKey).build();
                Role adminRole = Role.builder().name(UserRole.ROLE_ADMIN).build();
                Role superAdminRole = Role.builder().name(UserRole.ROLE_SUPER_ADMIN).build();

                when(userService.existsByEmail(any())).thenReturn(false);
                when(userService.existsByUsername(any())).thenReturn(false);
                when(userService.existsByPhoneNumber(any())).thenReturn(false);
                when(roleService.getOrCreate(UserRole.ROLE_ADMIN)).thenReturn(adminRole);
                when(roleService.getOrCreate(UserRole.ROLE_SUPER_ADMIN)).thenReturn(superAdminRole);
                when(userService.save(any(User.class))).thenReturn(dummyUser);

                mockedMapper.when(() -> AuthMapper.toUser(any(SuperAdminRegisterRequest.class), any(), any())).thenReturn(dummyUser);
                mockedMapper.when(() -> AuthMapper.toRegisterResponse(dummyUser)).thenReturn(RegisterResponse.builder().username("superadmin").build());

                // When
                RegisterResponse response = authService.registerSuperAdmin(request);

                // Then
                assertNotNull(response);
                assertEquals("superadmin", response.getUsername());
                verify(roleService).getOrCreate(UserRole.ROLE_ADMIN);
                verify(roleService).getOrCreate(UserRole.ROLE_SUPER_ADMIN);
                verify(userService).save(dummyUser);
            }
        }

        @Test
        @DisplayName("registerSuperAdmin(): secret key salah → INVALID_SUPERADMIN_KEY")
        void registerSuperAdmin_invalidKey_throwsException() {
            // Given
            ReflectionTestUtils.setField(authService, "actualSecretKey", "correct-key");
            SuperAdminRegisterRequest request = SuperAdminRegisterRequest.builder().secretKeySuperAdmin("wrong-key").build();

            // When & Then
            CustomException ex = assertThrows(CustomException.class, () -> authService.registerSuperAdmin(request));
            assertEquals(ErrorCode.INVALID_SUPERADMIN_KEY, ex.getErrorCode());
        }
    }
}