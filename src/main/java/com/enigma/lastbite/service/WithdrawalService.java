package com.enigma.lastbite.service;

import com.enigma.lastbite.dto.request.WithdrawalCreateRequest;
import com.enigma.lastbite.dto.response.WithdrawalResponse;

import java.util.List;

public interface WithdrawalService {
    WithdrawalResponse createRequest(WithdrawalCreateRequest req);
    WithdrawalResponse approveRequest(String id, String proofUrl);
    WithdrawalResponse rejectRequest(String id);
    WithdrawalResponse getById(String id);
    List<WithdrawalResponse> getMine();                    // seller
    List<WithdrawalResponse> getAll(String status);        // admin
}
