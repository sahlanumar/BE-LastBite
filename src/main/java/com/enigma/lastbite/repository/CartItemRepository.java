package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String>, JpaSpecificationExecutor<CartItem> {
}