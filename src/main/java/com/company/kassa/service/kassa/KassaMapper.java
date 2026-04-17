package com.company.kassa.service.kassa;

import com.company.kassa.dto.kassa.KassaCreateRequest;
import com.company.kassa.dto.kassa.KassaResponse;
import com.company.kassa.models.Kassa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class KassaMapper {

    @Mapping(target = "owner", ignore = true)
    public abstract Kassa toEntity(KassaCreateRequest request);

    public abstract KassaResponse mapToResponse(Kassa kassa);
}
