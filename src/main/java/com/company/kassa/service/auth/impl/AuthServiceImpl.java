package com.company.kassa.service.auth.impl;

import com.company.kassa.config.security.CustomUserDetailService;
import com.company.kassa.config.security.UserPrincipal;
import com.company.kassa.config.security.UserSession;
import com.company.kassa.config.security.jwt.JwtService;
import com.company.kassa.dto.AccessTokenRequest;
import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.LoginRequest;
import com.company.kassa.dto.LoginResponse;
import com.company.kassa.models.AuthUser;
import com.company.kassa.models.YaTTUsers;
import com.company.kassa.repository.AuthUserRepository;
import com.company.kassa.repository.YaTTRepository;
import com.company.kassa.repository.YaTTUsersRepository;
import com.company.kassa.service.auth.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final YaTTUsersRepository yaTTUsersRepository;
    private final YaTTRepository yaTTRepository;
    private final JwtService jwtService;
    private final UserSession userSession;
    private final CustomUserDetailService userDetailService;


    @Transactional
    @Override
    public HttpApiResponse<LoginResponse> login(LoginRequest request) {
        String password = request.getPassword();
        String username = request.getUsername();
        Long yattId = request.getYattId();

        yaTTRepository.findByIdAndDeletedAtIsNull(yattId)
                .orElseThrow(() -> new EntityNotFoundException("yatt.not.found"));

        AuthUser authUser = authUserRepository.findByUsernameAndYaTTId(username, yattId)
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        YaTTUsers yaTTUserRole = yaTTUsersRepository.findYattUserRole(username, yattId)
                .orElseThrow(() -> new EntityNotFoundException("role.not.found"));

        if (!passwordEncoder.matches(password, authUser.getPassword())) {
            throw new RuntimeException("password.or.username.incorrect");
        }

        Map<String, Object> claims = Map.of(
                "userId", authUser.getId(),
                "role", yaTTUserRole.getRole(),
                "yattId", yattId
        );

        LoginResponse response = LoginResponse.builder()
                .accessToken(jwtService.generateAccessToken(username, claims))
                .refreshToken(jwtService.generateRefreshToken(username, claims))
                .build();

        return HttpApiResponse.<LoginResponse>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(response)
                .build();
    }

    @Override
    public HttpApiResponse<LoginResponse> getAccessToken(AccessTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        String username = jwtService.extractUsername(refreshToken);
        Long yattId = jwtService.extractYattId(refreshToken);

        UserPrincipal user = (UserPrincipal)
                userDetailService.loadUserByUsernameAndYattId(username, yattId);

        if (!jwtService.isRefreshTokenValid(refreshToken, user)) {
            throw new RuntimeException("token.is.not.valid");
        }

        Map<String, Object> claims = Map.of(
                "userId", jwtService.extractUserId(refreshToken),
                "role", jwtService.extractRole(refreshToken),
                "yattId", yattId
        );

        String newAccessToken = jwtService.generateAccessToken(username, claims);

        LoginResponse response = LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();

        return HttpApiResponse.<LoginResponse>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(response)
                .build();
    }
}
