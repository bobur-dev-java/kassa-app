package com.company.kassa.service.kassa;

import com.company.kassa.dto.kassa.KassaCreateRequest;
import com.company.kassa.dto.kassa.KassaResponse;
import com.company.kassa.models.Kassa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class KassaMapper {

    public abstract Kassa toEntity(KassaCreateRequest request);

    @Mapping(target = "ownerName",source = "owner.fullName")
    @Mapping(target = "ownerId",source = "owner.id")
    public abstract KassaResponse mapToResponse(Kassa kassa);
}
