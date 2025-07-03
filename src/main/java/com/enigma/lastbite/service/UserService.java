package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.PasswordChangeRequest;
import com.enigma.lastbite.dto.request.UserUpdateRequest;
import com.enigma.lastbite.dto.request.UserFilterRequest;
import com.enigma.lastbite.dto.response.UserResponse;
import com.enigma.lastbite.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User findById(String id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<UserResponse> findAllUsers(
            UserFilterRequest filter,
            int page, int size,
            String sortField, String sortDir);

    UserResponse updateUserById(String id, UserUpdateRequest userUpdateRequest);

    UserResponse updateUser(UserUpdateRequest userUpdateRequest);

    UserResponse getUserByLogin();

    void deleteUserById(String id);

    UserResponse getUserById(String id);

    UserResponse updatePasswordById(String id, PasswordChangeRequest passwordChangeRequest);

    UserResponse updatePassword(PasswordChangeRequest passwordChangeRequest);
}
