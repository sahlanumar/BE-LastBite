package com.enigma.lastbite.entity;

import com.enigma.lastbite.constant.TableNames;
import com.enigma.lastbite.constant.WithdrawalStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = TableNames.WITHDRAWAL)
@Builder
public class WithdrawalRequest {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = false)
    private SellerProfile seller;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status;

    @CreationTimestamp
    @Column(name = "request_date", updatable = false)
    private OffsetDateTime requestDate;

    @Column(name = "processed_date")
    private OffsetDateTime processedDate;

    @ManyToOne
    @JoinColumn(name = "processed_by_id")
    private User processedBy;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "proof_of_payment_url")
    private String proofOfPaymentUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "cancel_reason")
    private String cancelReason;
}
