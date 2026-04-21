package com.company.kassa.dto.debit;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebitResponse {
    private Long fromUserId;
    private BigDecimal nonActive = BigDecimal.ZERO;
    private BigDecimal activeAmount = BigDecimal.ZERO;
}
