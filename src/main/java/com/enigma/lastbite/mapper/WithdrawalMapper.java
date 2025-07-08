package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.WithdrawalStatus;
import com.enigma.lastbite.dto.request.WithdrawalCreateRequest;
import com.enigma.lastbite.dto.response.WithdrawalResponse;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.WithdrawalRequest;


public class WithdrawalMapper {

    public static WithdrawalRequest toEntity(WithdrawalCreateRequest req, SellerProfile seller) {
        return WithdrawalRequest.builder()
                .seller(seller)
                .amount(req.getAmount())
                .status(WithdrawalStatus.PENDING)
                .accountNumber(req.getAccountNumber())
                .bankName(req.getBankName())
                .build();
    }

    public static WithdrawalResponse toResponse(WithdrawalRequest entity) {
        if (entity == null) {
            return null;
        }

        String sellerId = (entity.getSeller() != null) ? entity.getSeller().getId() : null;
        String processedByUsername = (entity.getProcessedBy() != null) ? entity.getProcessedBy().getUsername() : null;

        return WithdrawalResponse.builder()
                .id(entity.getId())
                .sellerId(sellerId)
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .requestDate(entity.getRequestDate())
                .processedDate(entity.getProcessedDate())
                .processedBy(processedByUsername)
                .bankName(entity.getBankName())
                .accountNumber(entity.getAccountNumber())
                .proofOfPaymentUrl(entity.getProofOfPaymentUrl())
                .balance(entity.getSeller().getBalance())
                .build();
    }
}