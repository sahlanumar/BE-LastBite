package com.enigma.lastbite.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalRejectRequest {
    @NotBlank(message = "Alasan wajib diisi")
    private String cancelReason;
}
