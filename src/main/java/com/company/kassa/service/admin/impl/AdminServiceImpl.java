package com.company.kassa.service.admin.impl;

import com.company.kassa.config.security.UserSession;
import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.SystemInfoResponse;
import com.company.kassa.dto.user.UserCreateRequest;
import com.company.kassa.dto.yatt.YattCreateRequest;
import com.company.kassa.models.AuthUser;
import com.company.kassa.models.YaTT;
import com.company.kassa.models.YaTTUsers;
import com.company.kassa.repository.AuthUserRepository;
import com.company.kassa.repository.YaTTRepository;
import com.company.kassa.repository.YaTTUsersRepository;
import com.company.kassa.service.admin.AdminService;
import com.company.kassa.service.auth.mapper.AuthMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AuthUserRepository authUserRepository;
    private final YaTTRepository yaTTRepository;
    private final UserSession session;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final YaTTUsersRepository yaTTUsersRepository;

    @Transactional
    @Override
    public HttpApiResponse<Long> createYatt(YattCreateRequest request) {
        String name = request.getName();

        if (yaTTRepository.existsByNameAndDeletedAtIsNull(name)) {
            throw new RuntimeException("yatt.already.exists");
        }

        YaTT yaTT = YaTT.builder()
                .name(name)
                .build();

        YaTT save = yaTTRepository.save(yaTT);

        return HttpApiResponse.<Long>builder()
                .success(true)
                .status(201)
                .message("ok")
                .data(save.getId())
                .build();
    }

    @Transactional
    @Override
    public HttpApiResponse<Long> addUser(Long yattId, UserCreateRequest request) {
        YaTT yaTT = yaTTRepository.findByIdAndDeletedAtIsNull(yattId)
                .orElseThrow(() -> new EntityNotFoundException("yatt.not.found"));

        AuthUser user = authMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setYattId(yattId);

        AuthUser saved = authUserRepository.save(user);

        YaTTUsers userRole = YaTTUsers.builder()
                .role(request.getRole())
                .user(saved)
                .yaTT(yaTT)
                .build();

        yaTTUsersRepository.save(userRole);

        return HttpApiResponse.<Long>builder()
                .success(true)
                .status(201)
                .message("ok")
                .data(saved.getId())
                .build();
    }

    @Override
    public HttpApiResponse<SystemInfoResponse> getSystemInfo() {
        return null;
    }
}
