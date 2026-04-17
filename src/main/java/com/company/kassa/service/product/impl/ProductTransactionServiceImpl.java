package com.company.kassa.service.product.impl;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.models.Product;
import com.company.kassa.models.ProductTransaction;
import com.company.kassa.service.product.ProductTransactionService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductTransactionServiceImpl implements ProductTransactionService {

    @Override
    public HttpApiResponse<Long> makeProdTransaction(ProductTransaction productTransaction, List<Product> products) {
        return null;
    }
}
