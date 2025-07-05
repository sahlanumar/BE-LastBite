package com.enigma.lastbite.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReportResponse {
    private long totalCustomer;
    private long totalSeller;
    private long totalSuccessTx;
    private BigDecimal totalSuccessAmount;
}
