package com.company.kassa.service.product;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.product.ProductTransactionUpdate;
import com.company.kassa.models.Product;
import com.company.kassa.models.ProductTransaction;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public interface ProductTransactionService {
    Long makeProdTransaction(ProductTransaction productTransaction, List<Product> products);

    void updateProductTransaction(ProductTransaction productTransaction, ProductTransactionUpdate request) throws AccessDeniedException;
}
