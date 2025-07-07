// src/main/java/com/enigma/lastbite/dto/response/WithdrawalResponse.java
package com.enigma.lastbite.dto.response;

import com.enigma.lastbite.constant.WithdrawalStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WithdrawalResponse {
    private String id;
    private String sellerId;
    private BigDecimal amount;
    private WithdrawalStatus status;
    private OffsetDateTime requestDate;
    private OffsetDateTime processedDate;
    private String bankName;
    private String accountNumber;
    private String processedBy;
    private String proofOfPaymentUrl;
    private BigDecimal balance;
}
