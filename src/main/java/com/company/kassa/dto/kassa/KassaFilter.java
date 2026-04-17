package com.company.kassa.dto.kassa;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KassaFilter {
    private Long ownerId;
    private LocalDate from;
    private LocalDate to;
    private Boolean isCompleted;

    public KassaFilter(Object o, LocalDate from, LocalDate to, Boolean isCompleted) {
        this.from = from;
        this.to = to;
        this.isCompleted = isCompleted;
    }
}
