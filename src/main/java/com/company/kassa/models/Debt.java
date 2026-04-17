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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Debt extends MultiTenant {
    @ManyToOne(fetch = FetchType.LAZY)
    private AuthUser fromUser;

    private BigDecimal nonActive = BigDecimal.ZERO;

    private BigDecimal activeAmount = BigDecimal.ZERO;
}
