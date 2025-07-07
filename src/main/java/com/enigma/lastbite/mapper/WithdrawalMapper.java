package com.enigma.lastbite.mapper;

import com.enigma.lastbite.constant.WithdrawalStatus;
import com.enigma.lastbite.dto.request.WithdrawalCreateRequest;
import com.enigma.lastbite.dto.response.WithdrawalResponse;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.WithdrawalRequest;

/**
 * Utility class untuk memetakan data antara DTO Withdrawal dan Entitas Withdrawal.
 * Versi ini sudah disesuaikan dengan semua kelas yang telah disediakan.
 */
public class WithdrawalMapper {
    
    /**
     * Memetakan WithdrawalCreateRequest DTO dan SellerProfile menjadi entitas WithdrawalRequest baru.
     * requestDate akan diisi otomatis oleh Hibernate berkat @CreationTimestamp.
     *
     * @param req    DTO yang berisi jumlah dana yang akan ditarik.
     * @param seller Entitas SellerProfile yang mengajukan permintaan.
     * @return Entitas WithdrawalRequest baru yang siap untuk disimpan.
     */
    public static WithdrawalRequest toEntity(WithdrawalCreateRequest req, SellerProfile seller) {
        return WithdrawalRequest.builder()
                .seller(seller)
                .amount(req.getAmount())
                .status(WithdrawalStatus.PENDING)
                .accountNumber(req.getAccountNumber())
                .bankName(req.getBankName())
                // requestDate tidak perlu di-set di sini, akan ditangani oleh @CreationTimestamp
                .build();
    }

    /**
     * Memetakan entitas WithdrawalRequest menjadi WithdrawalResponse DTO.
     *
     * @param entity Entitas WithdrawalRequest dari database.
     * @return DTO WithdrawalResponse.
     */
    public static WithdrawalResponse toResponse(WithdrawalRequest entity) {
        if (entity == null) {
            return null;
        }

        // Menangani jika relasi seller atau processedBy (admin) null untuk mencegah error
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