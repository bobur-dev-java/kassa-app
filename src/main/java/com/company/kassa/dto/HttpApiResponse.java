package com.company.kassa.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HttpApiResponse<T> implements Serializable {
    private boolean success;
    private int status;
    private String message;
    private T data;
}
