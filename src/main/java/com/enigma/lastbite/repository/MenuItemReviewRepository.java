package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.MenuItemReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemReviewRepository extends JpaRepository<MenuItemReview, String> {

    List<MenuItemReview> findAllByMenuItemId(String menuItemId);

    boolean existsByOrderIdAndMenuItemId(String orderId, String menuItemId);

    List<MenuItemReview> findAllByOrderId(String orderId);


}