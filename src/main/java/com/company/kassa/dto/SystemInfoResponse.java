package com.company.kassa.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemInfoResponse {
    private Integer userCount;
    private Integer yattCount;
}
