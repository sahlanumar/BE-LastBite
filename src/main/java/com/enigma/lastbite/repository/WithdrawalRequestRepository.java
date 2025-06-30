package com.enigma.lastbite.repository;

import com.enigma.lastbite.entity.WithdrawalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, String>, JpaSpecificationExecutor<WithdrawalRequest> {
}