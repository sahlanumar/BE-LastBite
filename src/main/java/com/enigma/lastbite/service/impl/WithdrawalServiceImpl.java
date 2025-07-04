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
import com.enigma.lastbite.service.WithdrawalService;
import com.enigma.lastbite.service.UserService;
import com.enigma.lastbite.service.CloudinaryService;       // jika ada
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRequestRepository withdrawalRepo;
    private final SellerProfileRepository sellerRepo;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final CloudinaryService cloudinaryService;     // optional kalau proof upload
    /* -------------------------------------------------- */

    /* ------------ Seller: create request -------------- */
    @Override
    public WithdrawalResponse createRequest(WithdrawalCreateRequest req) {

        // 1. Ambil current seller
        String username = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        SellerProfile seller = sellerRepo.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.SELLER_NOT_FOUND));

        // 2. Validasi saldo
        if (req.getAmount().compareTo(seller.getBalance()) > 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 3. Kurangi saldo lebih dulu (agar tidak double‑spend)
        seller.setBalance(seller.getBalance().subtract(req.getAmount()));
        sellerRepo.save(seller);

        // 4. Simpan request
        WithdrawalRequest entity = WithdrawalMapper.toEntity(req, seller);
        withdrawalRepo.save(entity);

        return WithdrawalMapper.toResponse(entity);
    }

    /* -------------- Admin: approve request ------------- */
    @Override
    public WithdrawalResponse approveRequest(String id, String proofUrl) {
        WithdrawalRequest wr = withdrawalRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

        if (wr.getStatus() != WithdrawalStatus.PENDING) {
            throw new CustomException(ErrorCode.STATUS_NOT_ALLOWED);
        }

        // Bukti transfer (upload dulu ke Cloudinary jika file, lalu set URL)
        wr.setProofOfPaymentUrl(proofUrl);

        // Siapa yang memproses?
        String adminUsername = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User admin = userService.findByUsername(adminUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        wr.setProcessedBy(admin);

        wr.setStatus(WithdrawalStatus.APPROVED);
        wr.setProcessedDate(OffsetDateTime.now());

        withdrawalRepo.save(wr);
        return WithdrawalMapper.toResponse(wr);
    }

    /* -------------- Admin: reject request -------------- */
    @Override
    public WithdrawalResponse rejectRequest(String id) {
        WithdrawalRequest wr = withdrawalRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_NOT_FOUND));

        if (wr.getStatus() != WithdrawalStatus.PENDING) {
            throw new CustomException(ErrorCode.STATUS_NOT_ALLOWED);
        }

        // Refund saldo ke seller
        SellerProfile seller = wr.getSeller();
        seller.setBalance(seller.getBalance().add(wr.getAmount()));
        sellerRepo.save(seller);

        wr.setStatus(WithdrawalStatus.REJECTED);
        wr.setProcessedDate(OffsetDateTime.now());

        // siapa yang memproses
        String adminUsername = jwtUtils.getUsernameFromJwtToken(jwtUtils.getTokenFromHeader());
        User admin = userService.findByUsername(adminUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        wr.setProcessedBy(admin);

        withdrawalRepo.save(wr);
        return WithdrawalMapper.toResponse(wr);
    }

    /* ----------------- Getters (list) ------------------ */
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
                .stream().map(WithdrawalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WithdrawalResponse> getAll(String status) {
        if (status == null) {
            return withdrawalRepo.findAll()
                    .stream().map(WithdrawalMapper::toResponse)
                    .collect(Collectors.toList());
        }
        WithdrawalStatus st = WithdrawalStatus.valueOf(status.toUpperCase());
        return withdrawalRepo.findAllByStatus(st)
                .stream().map(WithdrawalMapper::toResponse)
                .collect(Collectors.toList());
    }
}
