package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.dto.request.ChangePasswordRequest;
import com.enigma.lastbite.dto.request.UpdateUserRequest;
import com.enigma.lastbite.dto.request.UserFilterRequest;
import com.enigma.lastbite.dto.response.UserResponse;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.UserMapper;
import com.enigma.lastbite.repository.UserRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.UserService;
import com.enigma.lastbite.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<UserResponse> findAllUsers(
            UserFilterRequest filter,
            int page, int size,
            String sortField, String sortDir) {

        Sort sort = Sort.by(
                "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortField == null || sortField.isBlank() ? "createdAt" : sortField
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> users = userRepository.findAll(UserSpecification.build(filter), pageable);

        return users.map(UserMapper::toUserResponse);
    }

    @Override
    public UserResponse getUserByLogin(){
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        User user = findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUserById(String id, UpdateUserRequest updateUserRequest) {
        User user = findById(id);

        if(updateUserRequest.getFullName() != null) {
            user.setFullName(updateUserRequest.getFullName());
        }

        if(updateUserRequest.getEmail() != null) {
            user.setEmail(updateUserRequest.getEmail());
        }

        if(updateUserRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }
        if(updateUserRequest.getSuspendedUntil() != null) {
            user.setSuspendedUntil(updateUserRequest.getSuspendedUntil());

        }

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UpdateUserRequest updateUserRequest) {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User user = findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(updateUserRequest.getFullName() != null) {
            user.setFullName(updateUserRequest.getFullName());
        }

        if(updateUserRequest.getEmail() != null) {
            user.setEmail(updateUserRequest.getEmail());
        }

        if(updateUserRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(updateUserRequest.getPhoneNumber());
        }

        return UserMapper.toUserResponse(user);
    }

    @Override
    public void deleteUserById(String id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = findById(id);
        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updatePasswordById(String id, ChangePasswordRequest changePasswordRequest) {
        User user = findById(id);
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user.setPasswordHash(changePasswordRequest.getNewPassword());

        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updatePassword(ChangePasswordRequest changePasswordRequest) {
        String token = jwtUtils.getTokenFromHeader();
        jwtUtils.validateJwtToken(token);
        String username = jwtUtils.getUsernameFromJwtToken(token);

        User user = findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user.setPasswordHash(changePasswordRequest.getNewPassword());

        return UserMapper.toUserResponse(user);
    }
}
