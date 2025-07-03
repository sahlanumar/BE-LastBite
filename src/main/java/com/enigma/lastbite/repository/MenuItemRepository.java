package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String>, JpaSpecificationExecutor<MenuItem> {
    List<MenuItem> findAllByDisplayEndTimeBeforeAndQuantityAvailableGreaterThan(LocalDateTime currentTime, Integer quantity);

    @Modifying
    @Transactional
    @Query("UPDATE MenuItem m SET m.quantityAvailable = 0, m.status = 'NOT_AVAILABLE' WHERE m.displayEndTime < :now AND m.quantityAvailable > 0")
    void updateStatusForExpiredItems(LocalDateTime now);
}