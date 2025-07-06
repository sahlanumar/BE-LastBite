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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Query("""
    SELECT COUNT(o) FROM Order o
    WHERE o.sellerProfile.id = :sellerProfileId
    AND o.orderStatus = 'COMPLETED'
""")
    long countCompletedOrdersBySellerProfileId(@Param("sellerProfileId") String sellerProfileId);

    // =================== Tambahan untuk menghindari error PostgreSQL ===================

    @Query("""
        SELECT COUNT(o) FROM Order o
        WHERE o.orderStatus = :status
    """)
    long countByStatus(@Param("status") OrderStatus status);

    @Query("""
        SELECT COUNT(o) FROM Order o
        WHERE o.orderStatus = :status AND o.createdAt >= :start
    """)
    long countByStatusAndStart(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start
    );

    @Query("""
        SELECT COUNT(o) FROM Order o
        WHERE o.orderStatus = :status AND o.createdAt <= :end
    """)
    long countByStatusAndEnd(
            @Param("status") OrderStatus status,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COUNT(o) FROM Order o
        WHERE o.orderStatus = :status AND o.createdAt BETWEEN :start AND :end
    """)
    long countByStatusBetween(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // ==============================================

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderStatus = :status
    """)
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderStatus = :status AND o.createdAt >= :start
    """)
    BigDecimal sumTotalAmountByStatusAndStart(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start
    );

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderStatus = :status AND o.createdAt <= :end
    """)
    BigDecimal sumTotalAmountByStatusAndEnd(
            @Param("status") OrderStatus status,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.orderStatus = :status AND o.createdAt BETWEEN :start AND :end
    """)
    BigDecimal sumTotalAmountByStatusBetween(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
