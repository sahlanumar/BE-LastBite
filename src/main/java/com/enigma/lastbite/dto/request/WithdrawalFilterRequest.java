package com.enigma.lastbite.dto.request;

import com.enigma.lastbite.constant.WithdrawalStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WithdrawalFilterRequest {
    private WithdrawalStatus status;
    private LocalDateTime start;
    private LocalDateTime end;
}
