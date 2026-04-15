package com.company.kassa.service.admin;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.SystemInfoResponse;
import com.company.kassa.dto.user.UserCreateRequest;
import com.company.kassa.dto.yatt.YattCreateRequest;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {
    HttpApiResponse<Long> createYatt(YattCreateRequest request);

    HttpApiResponse<Long> addUser(Long yattId, UserCreateRequest request);

    HttpApiResponse<SystemInfoResponse> getSystemInfo();
}
