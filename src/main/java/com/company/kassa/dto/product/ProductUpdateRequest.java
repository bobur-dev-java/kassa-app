package com.company.kassa.dto.product;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateRequest {
    private Long id;
    private BigDecimal price;
    private String name;
    private Double quantity;


}
