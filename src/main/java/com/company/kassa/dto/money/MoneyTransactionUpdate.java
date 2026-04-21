package com.company.kassa.dto.money;

import com.company.kassa.models.enums.MoneyType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoneyTransactionUpdate {
    private BigDecimal amount;
    private MoneyType moneyType;
    private Boolean isCompleted;

}
