package com.company.kassa.dto.user;

import com.company.kassa.models.enums.YaTTUserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRequest implements Serializable {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private YaTTUserRole role;
}
