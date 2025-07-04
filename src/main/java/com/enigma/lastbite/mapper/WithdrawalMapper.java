package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.WithdrawalStatus;
import com.enigma.lastbite.dto.request.WithdrawalCreateRequest;
import com.enigma.lastbite.dto.response.WithdrawalResponse;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.entity.WithdrawalRequest;

public class WithdrawalMapper {

    /* ----- Seller membuat request ----- */
    public static WithdrawalRequest toEntity(
            WithdrawalCreateRequest req,
            SellerProfile seller
    ) {
        WithdrawalRequest entity = new WithdrawalRequest();
        entity.setSeller(seller);
        entity.setAmount(req.getAmount());
        entity.setStatus(WithdrawalStatus.PENDING);
        return entity;
    }

    /* ----- Entity → Response DTO ----- */
    public static WithdrawalResponse toResponse(WithdrawalRequest entity) {
        return WithdrawalResponse.builder()
                .id(entity.getId())
                .sellerId(entity.getSeller().getId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .requestDate(entity.getRequestDate())
                .processedDate(entity.getProcessedDate())
                .processedBy(
                        entity.getProcessedBy() != null ? entity.getProcessedBy().getUsername() : null
                )
                .proofOfPaymentUrl(entity.getProofOfPaymentUrl())
                .build();
    }
}
