package com.enigma.lastbite.specification;

import com.enigma.lastbite.constant.ListingStatus;
import com.enigma.lastbite.entity.MenuItem;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuItemSpecification {
    public static Specification<MenuItem> getSpecification(
            String name, String sellerId, BigDecimal maxPrice,
            BigDecimal minPrice, Boolean isAvailable, ListingStatus status) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                String pattern = "%" + name.trim().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern));
            }

            if (sellerId != null && !sellerId.isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("sellerProfile").get("id"), sellerId));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("discountedPrice"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("discountedPrice"), maxPrice));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (isAvailable != null && isAvailable) {
                predicates.add(criteriaBuilder.greaterThan(root.get("quantityAvailable"), 0));
                predicates.add(criteriaBuilder.equal(root.get("status"), ListingStatus.AVAILABLE));
            }

            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
    }
}