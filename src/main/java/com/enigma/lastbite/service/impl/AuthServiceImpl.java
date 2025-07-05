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
import com.enigma.lastbite.service.AuthService;
import com.enigma.lastbite.service.RoleService;
import com.enigma.lastbite.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final SellerProfileRepository sellerProfileRepository;

    @Value("${course.super-admin.secret-key}")
    private String actualSecretKey;

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        if (!StringUtils.hasText(loginRequest.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_REQUIRED);
        }
        String principal;
        if (StringUtils.hasText(loginRequest.getUsername())) {
            principal = loginRequest.getUsername();
        } else if (StringUtils.hasText(loginRequest.getEmail())) {
            principal = loginRequest.getEmail();
        } else {
            throw new CustomException(ErrorCode.USERNAME_OR_EMAIL_REQUIRED);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(principal, loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String accessToken = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(user.getId()).orElse(null);
        UserStatus status = null;
        if (sellerProfile != null) {
            status = sellerProfile.getStatus();
        }else {
            if(user.getSuspendedUntil() == null|| user.getSuspendedUntil().isBefore(LocalDateTime.now())) {
                status = UserStatus.ACTIVE;
            }else {
                status = UserStatus.INACTIVE;
            }
        }
        return JwtResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(status)
                .roles(roles)
                .build();
    }

    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        if (!jwtUtils.validateJwtToken(requestRefreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_NOT_VALID);
        }
        if (!jwtUtils.isRefreshToken(requestRefreshToken)) {
            throw new CustomException(ErrorCode.NOT_REFRESH_TOKEN);
        }
        String username = jwtUtils.getUsernameFromJwtToken(requestRefreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String newToken = jwtUtils.generateJwtToken(username, roles);
        return TokenRefreshResponse.builder()
                .accessToken(newToken)
                .refreshToken(requestRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public RegisterResponse registerCustomer(CustomerRegisterRequest customerRegisterRequest) {
        if (userService.existsByEmail(customerRegisterRequest.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userService.existsByUsername(customerRegisterRequest.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userService.existsByPhoneNumber(customerRegisterRequest.getPhoneNumber())) {
            throw new CustomException(ErrorCode.PHONENUMBER_ALREADY_EXISTS);
        }
        Set<Role> roles = new HashSet<>();
        Role customerRole = roleService.getOrCreate(UserRole.ROLE_CUSTOMER);
        roles.add(customerRole);
        User user = AuthMapper.toUser(customerRegisterRequest, passwordEncoder, roles);
        User savedUser = userService.save(user);

        return AuthMapper.toRegisterResponse(savedUser);
    }

    @Override
    @Transactional
    public RegisterResponse registerAdmin(AdminRegisterRequest adminRegisterRequest) {
        if (userService.existsByEmail(adminRegisterRequest.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userService.existsByUsername(adminRegisterRequest.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userService.existsByPhoneNumber(adminRegisterRequest.getPhoneNumber())) {
            throw new CustomException(ErrorCode.PHONENUMBER_ALREADY_EXISTS);
        }
        Set<Role> roles = new HashSet<>();
        Role adminRole = roleService.getOrCreate(UserRole.ROLE_ADMIN);
        roles.add(adminRole);
        User user = AuthMapper.toUser(adminRegisterRequest, passwordEncoder, roles);
        User savedUser = userService.save(user);

        return AuthMapper.toRegisterResponse(savedUser);
    }

    @Override
    @Transactional
    public RegisterResponse registerSeller(SellerRegisterRequest sellerRegisterRequest) {
        if (userService.existsByEmail(sellerRegisterRequest.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userService.existsByUsername(sellerRegisterRequest.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userService.existsByPhoneNumber(sellerRegisterRequest.getPhoneNumber())) {
            throw new CustomException(ErrorCode.PHONENUMBER_ALREADY_EXISTS);
        }
        Set<Role> roles = new HashSet<>();
        Role sellerRole = roleService.getOrCreate(UserRole.ROLE_SELLER);
        roles.add(sellerRole);
        User user = AuthMapper.toUser(sellerRegisterRequest, passwordEncoder, roles);
        User savedUser = userService.save(user);
        SellerProfile seller = AuthMapper.toSellerProfile(sellerRegisterRequest, savedUser);
        sellerProfileRepository.save(seller);

        return AuthMapper.toRegisterResponse(savedUser);
    }

    @Override
    @Transactional
    public RegisterResponse registerSuperAdmin(SuperAdminRegisterRequest request) {
        if (!actualSecretKey.equals(request.getSecretKeySuperAdmin())) {
            throw new CustomException(ErrorCode.INVALID_SUPERADMIN_KEY);
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userService.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userService.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomException(ErrorCode.PHONENUMBER_ALREADY_EXISTS);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getOrCreate(UserRole.ROLE_ADMIN));
        roles.add(roleService.getOrCreate(UserRole.ROLE_SUPER_ADMIN));

        User user = AuthMapper.toUser(request, passwordEncoder, roles);
        User savedUser = userService.save(user);

        return AuthMapper.toRegisterResponse(savedUser);
    }
}