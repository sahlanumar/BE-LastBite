package com.enigma.lastbite.specification;

import com.enigma.lastbite.constant.WithdrawalStatus;
import com.enigma.lastbite.entity.WithdrawalRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class WithdrawalSpecification {

    public static Specification<WithdrawalRequest> getSpecification(String status) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (status != null && !status.isBlank()) {
                try {
                    WithdrawalStatus withdrawalStatus = WithdrawalStatus.valueOf(status.toUpperCase());
                    predicate = cb.equal(root.get("status"), withdrawalStatus);
                } catch (IllegalArgumentException ignored) {
                    // status tidak valid, akan mengembalikan semua data (tanpa filter)
                }
            }

            return predicate;
        };
    }
}
