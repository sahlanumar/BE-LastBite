package com.enigma.lastbite.repository;

import com.enigma.lastbite.constant.WithdrawalStatus;
import com.enigma.lastbite.entity.WithdrawalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, String>, JpaSpecificationExecutor<WithdrawalRequest> {
    List<WithdrawalRequest> findAllByStatus(WithdrawalStatus status);

    List<WithdrawalRequest> findAllBySeller_User_Id(String userId);

    Page<WithdrawalRequest> findAllBySeller_User_Id(String userId, Pageable pageable);

    List<WithdrawalRequest> findAllBySeller_Id(String sellerProfileId);

}