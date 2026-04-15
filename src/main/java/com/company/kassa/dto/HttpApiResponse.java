package com.company.kassa.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpApiResponse<T> {
    private boolean success;
    private int status;
    private String message;
    private T data;
}
