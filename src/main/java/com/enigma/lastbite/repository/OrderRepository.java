package com.enigma.lastbite.repository;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {

    List<Order> findAllByCustomer_IdAndOrderStatus(String customerId, OrderStatus status);

    Page<Order> findAllByCustomer_Id(String customerId, Pageable pageable);

    Page<Order> findAllBySellerProfile_User_Id(String userId, Pageable pageable);

    @Query("""
    SELECT o FROM Order o
    JOIN o.orderItems oi
    WHERE o.customer.id = :customerId
      AND oi.menuItem.id = :menuItemId
      AND o.orderStatus = 'COMPLETED'
""")
    List<Order> findCompletedOrdersByCustomerAndMenuItem(
            @Param("customerId") String customerId,
            @Param("menuItemId") String menuItemId
    );
}