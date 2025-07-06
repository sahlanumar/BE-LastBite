package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.WithdrawalCreateRequest;
import com.enigma.lastbite.dto.request.WithdrawalFilterRequest;
import com.enigma.lastbite.dto.response.WithdrawalResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WithdrawalService {
    WithdrawalResponse createRequest(WithdrawalCreateRequest req);
    WithdrawalResponse approveRequest(String id, String proofUrl);
    WithdrawalResponse rejectRequest(String id);
    WithdrawalResponse getById(String id);
    Page<WithdrawalResponse> getMine(WithdrawalFilterRequest filter, int page, int size, String sortField, String sortDir);
    Page<WithdrawalResponse> getAllWithPagination(WithdrawalFilterRequest filter, int page, int size, String sortField, String sortDir);// admin
}
