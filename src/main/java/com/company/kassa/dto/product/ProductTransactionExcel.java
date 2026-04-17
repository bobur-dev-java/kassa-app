package com.company.kassa.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductTransactionExcel {
    private String fromUserName;
    private String toUserName;
    private LocalDate transactionDate;
    private BigDecimal totalPrice;
    private Boolean isCompleted;
    private List<ProductResponse> products;
}
