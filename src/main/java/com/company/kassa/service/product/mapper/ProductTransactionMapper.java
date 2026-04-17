package com.company.kassa.service.product.mapper;

import com.company.kassa.dto.product.ProductTransactionExcel;
import com.company.kassa.dto.product.ProductTransactionResponse;
import com.company.kassa.models.ProductTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ProductTransactionMapper {

    @Mapping(target = "toUserId", source = "transaction.toUser.id")
    @Mapping(target = "fromUserId", source = "transaction.fromUser.id")
    public abstract ProductTransactionResponse mapToRes(ProductTransaction transaction);

    @Mapping(target = "fromUserName",source = "transaction.fromUser.fullName")
    @Mapping(target = "toUserName",source = "transaction.toUser.fullName")
    public abstract ProductTransactionExcel mapToResExcel(ProductTransaction transaction);
}
