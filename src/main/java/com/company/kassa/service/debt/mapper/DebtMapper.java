package com.company.kassa.service.debt.mapper;

import com.company.kassa.dto.debit.DebitResponse;
import com.company.kassa.models.Debt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class DebtMapper {
    @Mapping(target = "fromUserId", source = "debt.fromUser.id")
    public abstract DebitResponse mapToRes(Debt debt);
}
