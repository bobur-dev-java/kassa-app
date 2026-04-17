package com.company.kassa.service.auth;

import com.company.kassa.dto.auth.AccessTokenRequest;
import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.auth.LoginRequest;
import com.company.kassa.dto.auth.LoginResponse;
import org.springframework.stereotype.Service;


@Service
public interface AuthService {
    HttpApiResponse<LoginResponse> login(LoginRequest request);

    HttpApiResponse<LoginResponse> getAccessToken(AccessTokenRequest request);
}
