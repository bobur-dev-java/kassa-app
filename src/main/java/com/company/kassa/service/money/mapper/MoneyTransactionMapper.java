package com.company.kassa.service.money.mapper;

import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.models.MoneyTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class MoneyTransactionMapper {

    @Mapping(target = "fromUserId",source = "transaction.fromUser.id")
    @Mapping(target = "toUserId",source = "transaction.toUser.id")
    @Mapping(target = "toUserFullName",source = "transaction.toUser.fullName")
    @Mapping(target = "fromUserFullName",source = "transaction.fromUser.fullName")
    public abstract MoneyTransactionResponse mapToRes(MoneyTransaction transaction);
}
