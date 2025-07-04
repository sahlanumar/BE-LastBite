package com.enigma.lastbite.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WithdrawalCreateRequest {

    @NotNull(message = "Jumlah penarikan wajib diisi")
    @DecimalMin(value = "1.00", message = "Minimal penarikan Rp1,00")
    @Digits(integer = 13, fraction = 2, message = "Format angka tidak valid (maks. 2 desimal)")
    private BigDecimal amount;
}
