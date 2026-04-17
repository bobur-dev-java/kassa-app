package com.company.kassa.dto.money;

import com.company.kassa.models.enums.MoneyType;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoneyTransactionRequest implements Serializable {
    private Long fromUserId;
    private Long toUserId;
    private LocalDate transactionDate;
    private BigDecimal amount;
    private MoneyType moneyType;
}
