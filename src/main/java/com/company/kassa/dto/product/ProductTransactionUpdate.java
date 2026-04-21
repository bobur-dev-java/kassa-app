package com.company.kassa.dto.product;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductTransactionUpdate {
    private LocalDate transactionDate;
    private Boolean isCompleted;
    List<ProductUpdateRequest> products;
}
