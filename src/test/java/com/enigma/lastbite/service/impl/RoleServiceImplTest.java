
package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.entity.Role;
import com.enigma.lastbite.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void getOrCreate_RoleExists() {
        Role role = new Role();
        role.setName(UserRole.ROLE_CUSTOMER);
        when(roleRepository.findByName(UserRole.ROLE_CUSTOMER)).thenReturn(Optional.of(role));

        Role result = roleService.getOrCreate(UserRole.ROLE_CUSTOMER);

        assertNotNull(result);
        assertEquals(UserRole.ROLE_CUSTOMER, result.getName());
    }

    @Test
    void getOrCreate_RoleNotExists() {
        when(roleRepository.findByName(UserRole.ROLE_CUSTOMER)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> {
            Role newRole = i.getArgument(0);
            newRole.setId("roleId");
            return newRole;
        });

        Role result = roleService.getOrCreate(UserRole.ROLE_CUSTOMER);

        assertNotNull(result);
        assertEquals(UserRole.ROLE_CUSTOMER, result.getName());
    }
}
