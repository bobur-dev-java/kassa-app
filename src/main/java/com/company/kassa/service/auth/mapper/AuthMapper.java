package com.company.kassa.service.auth.mapper;

import com.company.kassa.dto.user.UserCreateRequest;
import com.company.kassa.dto.user.UserResponse;
import com.company.kassa.dto.user.UserUpdateRequest;
import com.company.kassa.models.AuthUser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class AuthMapper {
    @Mapping(target = "password", ignore = true)
    public abstract AuthUser toEntity(UserCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract AuthUser updateEntity(@MappingTarget AuthUser currentUser, UserUpdateRequest request);

    public abstract UserResponse mapToUserResponse(AuthUser user);
}
