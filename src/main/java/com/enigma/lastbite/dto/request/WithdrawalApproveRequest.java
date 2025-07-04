// src/main/java/com/enigma/lastbite/dto/request/WithdrawalApproveRequest.java
package com.enigma.lastbite.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WithdrawalApproveRequest {

    /** URL bukti transfer yang sudah di‑upload ke Cloudinary */
    @NotBlank(message = "proofOfPaymentUrl wajib diisi")
    private String proofOfPaymentUrl;
}
