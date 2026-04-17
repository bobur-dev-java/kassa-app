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
public class Kassa extends MultiTenant {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private AuthUser owner;

    private BigDecimal terminal;
    private BigDecimal card;
    private BigDecimal cash;

    private BigDecimal totaAmount;

    private LocalDate kassaDate;

    @Column(length = 1000)
    private String comment;

    private Boolean isCompleted;
}
