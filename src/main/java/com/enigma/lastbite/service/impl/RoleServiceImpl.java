package com.enigma.lastbite.service.impl;


import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.entity.Role;
import com.enigma.lastbite.repository.RoleRepository;
import com.enigma.lastbite.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(UserRole name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role getOrCreate(UserRole name) {
        return findByName(name)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(name);
                    return save(newRole);
                });
    }
}
