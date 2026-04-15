package com.company.kassa.config.auditing;

import com.company.kassa.config.security.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {
    private final UserSession userSession;

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            try {
                return Optional.ofNullable(userSession.userId()); // usersession dan userId
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

}
