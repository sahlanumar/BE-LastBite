
package com.enigma.lastbite.service.impl;

import com.enigma.lastbite.constant.WithdrawalStatus;
import com.enigma.lastbite.dto.request.WithdrawalCreateRequest;
import com.enigma.lastbite.dto.response.WithdrawalResponse;
import com.enigma.lastbite.entity.SellerProfile;
import com.enigma.lastbite.entity.User;
import com.enigma.lastbite.entity.WithdrawalRequest;
import com.enigma.lastbite.repository.SellerProfileRepository;
import com.enigma.lastbite.repository.WithdrawalRequestRepository;
import com.enigma.lastbite.security.JwtUtils;
import com.enigma.lastbite.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceImplTest {

    @Mock
    private WithdrawalRequestRepository withdrawalRepo;

    @Mock
    private SellerProfileRepository sellerRepo;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private WithdrawalServiceImpl withdrawalService;

    private User user;
    private SellerProfile sellerProfile;
    private WithdrawalCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("userId");
        user.setUsername("testuser");

        sellerProfile = new SellerProfile();
        sellerProfile.setId("sellerId");
        sellerProfile.setUser(user);
        sellerProfile.setBalance(BigDecimal.valueOf(500000));

        createRequest = new WithdrawalCreateRequest();
        createRequest.setAmount(BigDecimal.valueOf(100000));
    }

    @Test
    void createRequest_Success() {
        when(jwtUtils.getTokenFromHeader()).thenReturn("token");
        when(jwtUtils.getUsernameFromJwtToken("token")).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(sellerRepo.findByUserId("userId")).thenReturn(Optional.of(sellerProfile));
        when(withdrawalRepo.save(any(WithdrawalRequest.class))).thenAnswer(i -> i.getArguments()[0]);

        WithdrawalResponse response = withdrawalService.createRequest(createRequest);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(100000), response.getAmount());
        assertEquals(WithdrawalStatus.PENDING, response.getStatus());
    }

    @Test
    void approveRequest_Success() {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setId("wdId");
        withdrawalRequest.setSeller(sellerProfile);
        withdrawalRequest.setAmount(BigDecimal.valueOf(100000));
        withdrawalRequest.setStatus(WithdrawalStatus.PENDING);

        when(withdrawalRepo.findById("wdId")).thenReturn(Optional.of(withdrawalRequest));
        when(jwtUtils.getTokenFromHeader()).thenReturn("token");
        when(jwtUtils.getUsernameFromJwtToken("token")).thenReturn("admin");
        when(userService.findByUsername("admin")).thenReturn(Optional.of(new User()));
        when(withdrawalRepo.save(any(WithdrawalRequest.class))).thenReturn(withdrawalRequest);

        WithdrawalResponse response = withdrawalService.approveRequest("wdId", "proof.jpg");

        assertNotNull(response);
        assertEquals(WithdrawalStatus.APPROVED, response.getStatus());
    }

    @Test
    void rejectRequest_Success() {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setId("wdId");
        withdrawalRequest.setSeller(sellerProfile);
        withdrawalRequest.setAmount(BigDecimal.valueOf(100000));
        withdrawalRequest.setStatus(WithdrawalStatus.PENDING);

        when(withdrawalRepo.findById("wdId")).thenReturn(Optional.of(withdrawalRequest));
        when(jwtUtils.getTokenFromHeader()).thenReturn("token");
        when(jwtUtils.getUsernameFromJwtToken("token")).thenReturn("admin");
        when(userService.findByUsername("admin")).thenReturn(Optional.of(new User()));
        when(withdrawalRepo.save(any(WithdrawalRequest.class))).thenReturn(withdrawalRequest);

        WithdrawalResponse response = withdrawalService.rejectRequest("wdId", "reason");

        assertNotNull(response);
        assertEquals(WithdrawalStatus.REJECTED, response.getStatus());
    }
}
