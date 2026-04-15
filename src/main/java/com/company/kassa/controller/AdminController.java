package com.company.kassa.controller;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.SystemInfoResponse;
import com.company.kassa.dto.user.UserCreateRequest;
import com.company.kassa.dto.yatt.YattCreateRequest;
import com.company.kassa.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system-admin")
@PreAuthorize("hasRole('SMALL_SELLER')")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/yatt")
    public ResponseEntity<HttpApiResponse<Long>> createYatt(@Valid @RequestBody YattCreateRequest request) {
        HttpApiResponse<Long> response = adminService.createYatt(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/{yattId}")
    public ResponseEntity<HttpApiResponse<Long>> addUser(@PathVariable Long yattId, @Valid UserCreateRequest request) {
        HttpApiResponse<Long> response = adminService.addUser(yattId, request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<HttpApiResponse<SystemInfoResponse>> getSystemInfo() {
        HttpApiResponse<SystemInfoResponse> response = adminService.getSystemInfo();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
