package com.company.kassa.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest implements Serializable {
    @NotNull
    private Long yattId;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
