package com.company.kassa.controller;

import com.company.kassa.dto.AccessTokenRequest;
import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.LoginRequest;
import com.company.kassa.dto.LoginResponse;
import com.company.kassa.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<HttpApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        HttpApiResponse<LoginResponse> response = authService.login(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/access-token")
    public ResponseEntity<HttpApiResponse<LoginResponse>> getAccessToken(@RequestBody @Valid AccessTokenRequest request) {
        HttpApiResponse<LoginResponse> response = authService.getAccessToken(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
