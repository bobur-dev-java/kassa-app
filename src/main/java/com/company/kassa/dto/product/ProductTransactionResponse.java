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
public class ProductTransactionResponse implements Serializable {
    private Long fromUserId;
    private Long toUserId;
    private LocalDate transactionDate;
    private BigDecimal totalPrice;
    private Boolean isCompleted;
    private List<ProductResponse> products;
}
