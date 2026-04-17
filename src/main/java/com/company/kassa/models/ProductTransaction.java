package com.company.kassa.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private Boolean isCompleted;

}
