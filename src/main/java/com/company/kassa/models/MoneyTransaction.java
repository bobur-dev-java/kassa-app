package com.company.kassa.models;

import com.company.kassa.models.enums.MoneyType;
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
public class MoneyTransaction extends MultiTenant {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private AuthUser fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private AuthUser toUser;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoneyType moneyType;

    @Builder.Default
    private Boolean isCompleted=false;
}
