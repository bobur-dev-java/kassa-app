package com.company.kassa.config.security;

import com.company.kassa.models.AuthUser;
import com.company.kassa.repository.AuthUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSession {
    private final AuthUserRepository authUserRepository;

    private UserPrincipal getPrincipal() {
        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal user)) {
            throw new RuntimeException("No authenticated user found in session");
        }

        return user;
    }

    public Long userId() {
        return getPrincipal().getUserId();
    }

    public Long yattId() {
        return getPrincipal().getYattId();
    }

    public UserPrincipal principal() {
        return getPrincipal();
    }

    public AuthUser getCurrentUser() {
        Long userId = getPrincipal().getUserId();
        Long yattId = yattId();
        return authUserRepository.findByIdAndYattIdAndDeletedAtIsNull(userId, yattId).
                orElseThrow(() -> new EntityNotFoundException("user.not.found"));
    }
}
