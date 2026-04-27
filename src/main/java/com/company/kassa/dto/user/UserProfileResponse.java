package com.company.kassa.dto.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String fullName;
    private String username;
    private String role;
}
