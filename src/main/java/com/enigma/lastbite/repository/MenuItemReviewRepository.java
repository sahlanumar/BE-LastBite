package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.MenuItemReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemReviewRepository extends JpaRepository<MenuItemReview, String>, JpaSpecificationExecutor<MenuItemReview> {
}