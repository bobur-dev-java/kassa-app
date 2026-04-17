package com.company.kassa.dto.money;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoneyTransactionResponse implements Serializable {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private String moneyType;
    private Boolean isCompleted;
}
