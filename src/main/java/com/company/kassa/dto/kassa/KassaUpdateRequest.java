package com.company.kassa.dto.kassa;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KassaUpdateRequest {
    private BigDecimal terminal;
    private BigDecimal card;
    private BigDecimal cash;
    private LocalDate kassaDate;
    private String comment;
    private Boolean isCompleted;

}
