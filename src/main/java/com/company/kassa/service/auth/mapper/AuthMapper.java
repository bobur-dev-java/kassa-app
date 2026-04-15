package com.company.kassa.service.auth.mapper;

import com.company.kassa.dto.user.UserCreateRequest;
import com.company.kassa.models.AuthUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AuthMapper {
    @Mapping(target = "password", ignore = true)
    public abstract AuthUser toEntity(UserCreateRequest request);
}
