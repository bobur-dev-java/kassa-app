package com.company.kassa.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class ProductTransaction extends MultiTenant {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private AuthUser fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private AuthUser toUser;

    @Column(nullable = false)
    private LocalDate transactionDate;

    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Builder.Default
    private Boolean isCompleted=false;

}
