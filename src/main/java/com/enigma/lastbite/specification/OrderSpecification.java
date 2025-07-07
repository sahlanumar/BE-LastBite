package com.enigma.lastbite.specification;

import com.enigma.lastbite.constant.OrderStatus;
import com.enigma.lastbite.dto.request.OrderFilterRequest;
import com.enigma.lastbite.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {
    public static Specification<Order> hasCustomer(String customerId) {
        return (root, query, cb) ->
                customerId == null ? cb.conjunction() : cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Order> hasSeller(String sellerId) {
        return (root, query, cb) ->
                sellerId == null ? cb.conjunction() : cb.equal(root.get("sellerProfile").get("id"), sellerId);
    }



    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("orderStatus"), status);
    }

    public static Specification<Order> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null)
                return cb.between(root.get("createdAt"), from, to);
            return from != null
                    ? cb.greaterThanOrEqualTo(root.get("createdAt"), from)
                    : cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    public static Specification<Order> build(OrderFilterRequest f) {
        return Specification.where(hasCustomer(f.getCustomerId()))
                .and(hasSeller(f.getSellerId()))
                .and(hasStatus(f.getStatus()))
                .and(createdBetween(f.getCreatedFrom(), f.getCreatedTo()));
    }
}
