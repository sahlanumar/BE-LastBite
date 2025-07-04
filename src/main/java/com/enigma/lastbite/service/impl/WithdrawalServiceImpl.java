package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.WithdrawalStatus;
import com.enigma.lastbite.dto.request.WithdrawalCreateRequest;
import com.enigma.lastbite.dto.response.WithdrawalResponse;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.entity.WithdrawalRequest;
import com.enigma.lastbite.exception.CustomException;
import com.enigma.lastbite.exception.ErrorCode;
import com.enigma.lastbite.mapper.WithdrawalMapper;
import com.enigma.lastbite.repository.SellerProfileRepository;
import com.enigma.lastbite.repository.WithdrawalRequestRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.UserService;
import com.enigma.lastbite.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRequestRepository withdrawalRepo;
    private final SellerProfileRepository sellerRepo;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    // Hapus CloudinaryService jika tidak dipakai untuk upload bukti
    // private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public WithdrawalResponse createRequest(WithdrawalCreateRequest req) {
        log.info("Starting create withdrawal request for amount: {}", req.getAmount());

        // 1. Dapatkan seller yang sedang login
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        SellerProfile seller = sellerRepo.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND));
        log.info("Seller found: {}", seller.getStoreName());

        // 2. Validasi apakah saldo mencukupi
        if (req.getAmount().compareTo(seller.getBalance()) > 0) {
            log.error("Insufficient balance for seller {}. Requested: {}, Available: {}", seller.getId(), req.getAmount(), seller.getBalance());
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 3. Kurangi saldo seller terlebih dahulu untuk mencegah double-spending
        seller.setBalance(seller.getBalance().subtract(req.getAmount()));
        sellerRepo.saveAndFlush(seller);
        log.info("Seller balance updated. New balance: {}", seller.getBalance());

        // 4. Buat dan simpan entitas withdrawal request menggunakan mapper
        WithdrawalRequest entity = WithdrawalMapper.toEntity(req, seller);
        withdrawalRepo.save(entity);
        log.info("Withdrawal request created with id: {}", entity.getId());

        // 5. Kembalikan response menggunakan mapper
        return WithdrawalMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public WithdrawalResponse approveRequest(String id, String proofUrl) {
        log.info("Approving withdrawal request with id: {}", id);
        // 1. Cari request berdasarkan ID
        WithdrawalRequest wr = withdrawalRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

        // 2. Pastikan statusnya masih PENDING
        if (wr.getStatus() != WithdrawalStatus.PENDING) {
            log.warn("Attempted to approve a request with non-PENDING status: {}", wr.getStatus());
            throw new CustomException(ErrorCode.STATUS_NOT_ALLOWED);
        }

        // 3. Dapatkan admin yang memproses
        String adminUsername = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User admin = userService.findByUsername(adminUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 4. Update status dan data terkait
        wr.setStatus(WithdrawalStatus.APPROVED);
        wr.setProcessedBy(admin);
        wr.setProcessedDate(OffsetDateTime.now());
        wr.setProofOfPaymentUrl(proofUrl); // Set URL bukti transfer

        withdrawalRepo.save(wr);
        log.info("Request {} approved by admin {}", id, adminUsername);
        return WithdrawalMapper.toResponse(wr);
    }

    @Override
    @Transactional
    public WithdrawalResponse rejectRequest(String id) {
        log.info("Rejecting withdrawal request with id: {}", id);
        // 1. Cari request berdasarkan ID
        WithdrawalRequest wr = withdrawalRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

        // 2. Pastikan statusnya masih PENDING
        if (wr.getStatus() != WithdrawalStatus.PENDING) {
            log.warn("Attempted to reject a request with non-PENDING status: {}", wr.getStatus());
            throw new CustomException(ErrorCode.STATUS_NOT_ALLOWED);
        }

        // 3. Kembalikan dana ke saldo seller
        SellerProfile seller = wr.getSeller();
        seller.setBalance(seller.getBalance().add(wr.getAmount()));
        sellerRepo.save(seller);
        log.info("Balance refunded to seller {}. Amount: {}", seller.getId(), wr.getAmount());

        // 4. Dapatkan admin yang memproses
        String adminUsername = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User admin = userService.findByUsername(adminUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 5. Update status dan data terkait
        wr.setStatus(WithdrawalStatus.REJECTED);
        wr.setProcessedBy(admin);
        wr.setProcessedDate(OffsetDateTime.now());

        withdrawalRepo.save(wr);
        log.info("Request {} rejected by admin {}", id, adminUsername);
        return WithdrawalMapper.toResponse(wr);
    }

    @Override
    @Transactional(readOnly = true)
    public WithdrawalResponse getById(String id) {
        WithdrawalRequest wr = withdrawalRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));
        return WithdrawalMapper.toResponse(wr);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WithdrawalResponse> getMine() {
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return withdrawalRepo.findAllBySeller_User_Id(user.getId())
                .stream()
                .map(WithdrawalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WithdrawalResponse> getAll(String status) {
        List<WithdrawalRequest> requests;
        if (status == null || status.isBlank()) {
            requests = withdrawalRepo.findAll();
        } else {
            try {
                WithdrawalStatus st = WithdrawalStatus.valueOf(status.toUpperCase());
                requests = withdrawalRepo.findAllByStatus(st);
            } catch (IllegalArgumentException e) {
                // Handle jika status yang dimasukkan tidak valid
                throw new CustomException(ErrorCode.INVALID_STATUS_FILTER);
            }
        }
        return requests.stream()
                .map(WithdrawalMapper::toResponse)
                .collect(Collectors.toList());
    }
}