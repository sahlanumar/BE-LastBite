package com.enigma.lastbite.specification;

import com.enigma.lastbite.dto.request.WithdrawalFilterRequest;
import com.enigma.lastbite.entity.WithdrawalRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class WithdrawalSpecification {

    /**
     * Membuat Specification untuk WithdrawalRequest berdasarkan filter dan userId (opsional).
     * @param filter DTO yang berisi kriteria filter seperti status dan rentang tanggal.
     * @param userId ID dari user yang melakukan request (untuk metode getMine). Bisa null jika tidak diperlukan (untuk getAll).
     * @return Specification<WithdrawalRequest>
     */
    public static Specification<WithdrawalRequest> getSpecification(WithdrawalFilterRequest filter, String userId) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (userId != null && !userId.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(root.get("seller").get("user").get("id"), userId));
            }

            if (filter.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getStart() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getStart()));
            }

            if (filter.getEnd() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), filter.getEnd()));
            }

            return predicate;
        };
    }
}