package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    Page<Order> findAllByCustomer_Id(String customerId, Pageable pageable);

    Page<Order> findAllBySellerProfile_User_Id(String userId, Pageable pageable);
}