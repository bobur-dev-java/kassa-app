package com.company.kassa.service.product.impl;

import com.company.kassa.config.security.UserSession;
import com.company.kassa.models.Debt;
import com.company.kassa.models.Product;
import com.company.kassa.models.ProductTransaction;
import com.company.kassa.repository.DebitRepository;
import com.company.kassa.repository.ProductRepository;
import com.company.kassa.repository.ProductTransactionRepository;
import com.company.kassa.service.product.ProductTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductTransactionServiceImpl implements ProductTransactionService {
    private final ProductTransactionRepository productTransactionRepository;
    private final ProductRepository productRepository;
    private final DebitRepository debitRepository;
    private final UserSession userSession;

    @Override
    public Long makeProdTransaction(ProductTransaction productTransaction, List<Product> products) {
        // 1. Calculate the grand total while updating each product
        BigDecimal grandTotal = products.stream()
                .map(product -> {
                    product.calculateTotalPrice(); // Set product's internal total
                    product.setProductTransaction(productTransaction); // Link to transaction
                    product.setYattId(userSession.yattId());
                    return product.getTotalPrice(); // Return for summing
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Set the total price on the parent transaction
        productTransaction.setTotalPrice(grandTotal);

        // 3. Persist the parent (Transaction) first
        ProductTransaction savedTx = productTransactionRepository.save(productTransaction);

        // 4. Save all children (Products)
        // Note: Since we linked them in step 1, they are ready to go.
        productRepository.saveAll(products);

        createOrUpdateDebit(savedTx);

        return savedTx.getId();
    }

    private void createOrUpdateDebit(ProductTransaction savedTx) {
        Long userId = savedTx.getFromUser().getId();

        Debt debt = debitRepository.findByUserId(userId, userSession.yattId())
                .map(existing -> {
                    existing.setActiveAmount(
                            existing.getActiveAmount().add(savedTx.getTotalPrice())
                    );
                    return existing;
                })
                .orElseGet(() -> Debt.builder()
                        .activeAmount(savedTx.getTotalPrice())
                        .fromUser(savedTx.getFromUser())
                        .yattId(userSession.yattId())
                        .build()
                );

        debitRepository.save(debt);
    }
}
