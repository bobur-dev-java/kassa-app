package com.company.kassa.config.security;

import com.company.kassa.config.corsConfig.CorsConfig;
import com.company.kassa.config.security.jwt.JwtFilter;
import com.company.kassa.dto.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsConfig corsConfig;
    private final ObjectMapper objectMapper;
    private final JwtFilter jwtFilter;

    private static final String[] WHITELIST = {
            "/api/auth/login/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/swagger-ui/swagger-config"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(WHITELIST).permitAll()
                                .anyRequest()
                                .fullyAuthenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            ErrorDto errorDto = new ErrorDto(
                    request.getRequestURI(),
                    authException.getMessage(),
                    HttpStatus.UNAUTHORIZED.value()
            );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ServletOutputStream out = response.getOutputStream();
            objectMapper.writeValue(out, errorDto);
            out.flush();
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            ErrorDto errorDto = new ErrorDto(
                    request.getRequestURI(),
                    ex.getMessage(),
                    HttpStatus.FORBIDDEN.value()
            );
            response.setStatus(HttpStatus.FORBIDDEN.value());
            ServletOutputStream out = response.getOutputStream();
            objectMapper.writeValue(out, errorDto);
            out.flush();
        };
    }
}
