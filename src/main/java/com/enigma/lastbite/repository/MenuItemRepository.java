package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String>, JpaSpecificationExecutor<MenuItem> {
}