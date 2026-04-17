package com.company.kassa.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SystemInfoResponse implements Serializable {
    private Integer userCount;
    private Integer yattCount;
}
