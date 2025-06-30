package com.enigma.lastbite.service;


import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.entity.Role;

import java.util.Optional;

public interface RoleService {
    Optional<Role> findByName(UserRole name);

    Role save(Role role);

    Role getOrCreate(UserRole name);
}
