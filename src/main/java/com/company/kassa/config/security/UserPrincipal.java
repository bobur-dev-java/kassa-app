package com.company.kassa.config.security;


import com.company.kassa.models.AuthUser;
import com.company.kassa.models.YaTTUsers;
import com.company.kassa.models.enums.YaTTUserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    @Getter
    private final Long userId;
    @Getter
    private final Long yattId;
    private final YaTTUserRole role;
    private final String username;
    private final String password;

    public UserPrincipal(AuthUser user, YaTTUsers role) {
        this.userId = user.getId();
        this.yattId = user.getYattId();
        this.role = role.getRole();
        this.username = user.getUsername();
        this.password = user.getPassword();

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }


}