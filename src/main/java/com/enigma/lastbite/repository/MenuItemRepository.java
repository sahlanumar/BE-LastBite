package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String>, JpaSpecificationExecutor<MenuItem> {
    List<MenuItem> findAllByDisplayEndTimeBeforeAndQuantityAvailableGreaterThan(LocalDateTime currentTime, Integer quantity);
}