package com.enigma.lastbite.specification;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.dto.request.OrderFilterRequest;
import com.enigma.lastbite.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {
    public static Specification<Order> hasCustomer(String customerId) {
        return (root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Order> hasSeller(String sellerId) {
        return (root, query, cb) -> cb.equal(root.get("sellerProfile").get("id"), sellerId);
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("orderStatus"), status);
    }

    public static Specification<Order> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from, to);
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            }
            if (to != null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), to);
            }
            return null;
        };
    }

    public static Specification<Order> build(OrderFilterRequest filter) {
        Specification<Order> spec = Specification.where(null);

        if (filter.getCustomerId() != null && !filter.getCustomerId().isBlank()) {
            spec = spec.and(hasCustomer(filter.getCustomerId()));
        }
        if (filter.getSellerId() != null && !filter.getSellerId().isBlank()) {
            spec = spec.and(hasSeller(filter.getSellerId()));
        }
        if (filter.getStatus() != null) {
            spec = spec.and(hasStatus(filter.getStatus()));
        }
        if (filter.getCreatedFrom() != null || filter.getCreatedTo() != null) {
            spec = spec.and(createdBetween(filter.getCreatedFrom(), filter.getCreatedTo()));
        }

        return spec;
    }
}
