package com.company.kassa.config.security;

import com.company.kassa.utils.TenantContext;
import com.company.kassa.models.YaTTUsers;
import com.company.kassa.repository.AuthUserRepository;
import com.company.kassa.repository.YaTTRepository;
import com.company.kassa.repository.YaTTUsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final AuthUserRepository authUserRepository;
    private final YaTTRepository yattRepository;
    private final YaTTUsersRepository yattUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long yattId = TenantContext.getTenantId();
        if (yattId == null) {
            throw new UsernameNotFoundException("Tenant not resolved");
        }
        return null;
    }

    @Transactional
    public UserDetails loadUserByUsernameAndYattId(String username, Long yattId) {
        yattRepository.findByIdAndDeletedAtIsNull(yattId)
                .orElseThrow(() -> new EntityNotFoundException("yatt.not.found"));

        authUserRepository.findByUsernameAndYaTTId(username, yattId)
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        YaTTUsers userRole = yattUserRepository.findYattUserRole(username, yattId)
                .orElseThrow(() -> new EntityNotFoundException("yatt.role.not.found"));

        return new UserPrincipal(userRole.getUser(), userRole);
    }

}
