package com.company.kassa.dto.product;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Builder
public class ProductTransactionFilter {
    private Long fromUserId;
    private Long toUserId;
    private LocalDate from;
    private LocalDate to;
    private Boolean isCompleted;
}
