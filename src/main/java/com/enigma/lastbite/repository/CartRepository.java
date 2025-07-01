package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String>, JpaSpecificationExecutor<Cart> {
    Optional<Cart> findByCustomerId(String customerId);
}
