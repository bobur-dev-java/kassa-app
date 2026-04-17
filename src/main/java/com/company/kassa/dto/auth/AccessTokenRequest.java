package com.company.kassa.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessTokenRequest implements Serializable {
    @NotBlank
    private String refreshToken;
}
