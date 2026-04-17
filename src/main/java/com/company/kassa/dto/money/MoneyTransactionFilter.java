package com.company.kassa.dto.money;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoneyTransactionFilter {
    private Long fromUserId;
    private Long toUserId;
    private String moneyType;
    private LocalDate from;
    private LocalDate to;
    private Boolean isCompleted;
}
