package com.company.kassa.config;

import com.company.kassa.models.AuthUser;
import com.company.kassa.models.YaTT;
import com.company.kassa.models.YaTTUsers;
import com.company.kassa.models.enums.YaTTUserRole;
import com.company.kassa.repository.AuthUserRepository;
import com.company.kassa.repository.YaTTRepository;
import com.company.kassa.repository.YaTTUsersRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.TimeZone;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final AuthUserRepository authUserRepository;
    private final YaTTRepository yaTTRepository;
    private final YaTTUsersRepository yaTTUsersRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Transactional
    public ApplicationRunner initData() {
        return args -> {
            if (authUserRepository.count() > 0) return;
            YaTT yaTT = YaTT.builder()
                    .name("AsilPista")
                    .build();
            yaTTRepository.save(yaTT);

            AuthUser user = AuthUser.builder()
                    .username("systemadmin")
                    .password(passwordEncoder.encode("123456"))
                    .yattId(yaTT.getId())
                    .build();
            authUserRepository.save(user);

            YaTTUsers userRole = YaTTUsers.builder()
                    .user(user)
                    .yaTT(yaTT)
                    .role(YaTTUserRole.ADMIN)
                    .build();
            yaTTUsersRepository.save(userRole);
            log.info("🔥 DEFAULT SYSTEM ADMIN CREATED");
            log.info("username: systemadmin");
            log.info("password: 123456");
        };
    }

    @PostConstruct
    public void initTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tashkent"));
    }
}
