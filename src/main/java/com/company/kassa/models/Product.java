package com.company.kassa.models;

import jakarta.persistence.*;
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
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private ProductTransaction productTransaction;


    public void calculateTotalPrice() {
        if (price == null || quantity == null) {
            this.totalPrice = BigDecimal.ZERO;
        } else {
            this.totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
