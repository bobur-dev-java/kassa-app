package com.company.kassa.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
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
    private AuthUser fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private AuthUser toUser;

    private LocalDate transactionDate;

    private BigDecimal totalPrice;

    private boolean isCompleted;

}
