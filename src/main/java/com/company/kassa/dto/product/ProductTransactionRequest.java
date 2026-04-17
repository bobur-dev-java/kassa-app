package com.company.kassa.dto.product;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductTransactionRequest implements Serializable {
    private Long fromUserId;
    private Long toUserId;
    private LocalDate transactionDate;
    private List<ProductRequest> products;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductRequest {
        private BigDecimal price;
        private String name;
        private Double quantity;
    }

}
