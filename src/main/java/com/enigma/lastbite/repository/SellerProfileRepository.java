package com.enigma.lastbite.repository;

import com.enigma.lastbite.constant.UserStatus;
import com.enigma.lastbite.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, String>, JpaSpecificationExecutor<SellerProfile> {
    Optional<SellerProfile> findByUserId(String userId);
    long countByStatus(UserStatus status);
}