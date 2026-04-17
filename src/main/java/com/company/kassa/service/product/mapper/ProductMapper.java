package com.company.kassa.service.product.mapper;

import com.company.kassa.dto.product.ProductResponse;
import com.company.kassa.dto.product.ProductTransactionRequest;
import com.company.kassa.models.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    public abstract Product toEntity(ProductTransactionRequest.ProductRequest request);

    public abstract ProductResponse mapToResponse(Product product);
}
