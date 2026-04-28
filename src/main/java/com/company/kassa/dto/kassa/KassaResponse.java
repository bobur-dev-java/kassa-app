package com.company.kassa.dto.kassa;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KassaResponse {
    private Long id;
    private String ownerName;
    private Long ownerId;
    private BigDecimal terminal;
    private BigDecimal card;
    private BigDecimal cash;
    private BigDecimal totaAmount;
    private LocalDate kassaDate;
    private String comment;
    private boolean isCompleted;
}
