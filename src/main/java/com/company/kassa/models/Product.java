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
public class Product extends MultiTenant {
    private BigDecimal price;
    private String name;
    private Double quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductTransaction productTransaction;
}
