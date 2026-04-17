package com.company.kassa.dto.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPasswordUpdate {
    private String newPassword;
    private String oldPassword;
}
