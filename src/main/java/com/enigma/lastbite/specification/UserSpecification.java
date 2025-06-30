package com.enigma.lastbite.specification;

import com.enigma.lastbite.constant.UserRole;
import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.dto.request.UserFilterRequest;
import com.enigma.lastbite.entity.Role;
import com.enigma.lastbite.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class UserSpecification {

    public static Specification<User> hasRole(UserRole role) {
        return (root, query, cb) ->
                role == null ? cb.conjunction()
                        : cb.equal(root.join("role").get("name"), role);
    }

    public static Specification<User> hasStatus(UserStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }

    public static Specification<User> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null)
                return cb.between(root.get("createdAt"), from, to);
            return from != null
                    ? cb.greaterThanOrEqualTo(root.get("createdAt"), from)
                    : cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    /** search by username/fullName/email (ilike) */
    public static Specification<User> containsKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            String like = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("username")), like),
                    cb.like(cb.lower(root.get("fullName")), like),
                    cb.like(cb.lower(root.get("email")), like)
            );
        };
    }

    /** helper untuk merangkai semuanya */
    public static Specification<User> build(UserFilterRequest f) {
        return Specification.where(hasRole(f.getRole()))
                .and(hasStatus(f.getStatus()))
                .and(createdBetween(f.getCreatedFrom(), f.getCreatedTo()))
                .and(containsKeyword(f.getSearch()));
    }
}
