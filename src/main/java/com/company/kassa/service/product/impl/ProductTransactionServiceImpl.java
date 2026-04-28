package com.company.kassa.service.product.impl;

import com.company.kassa.config.security.UserSession;
import com.company.kassa.dto.product.ProductTransactionUpdate;
import com.company.kassa.dto.product.ProductUpdateRequest;
import com.company.kassa.models.AuthUser;
import com.company.kassa.models.Debt;
import com.company.kassa.models.Product;
import com.company.kassa.models.ProductTransaction;
import com.company.kassa.repository.DebitRepository;
import com.company.kassa.repository.ProductRepository;
import com.company.kassa.repository.ProductTransactionRepository;
import com.company.kassa.service.product.ProductTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class ProductTransactionServiceImpl implements ProductTransactionService {

    private final ProductTransactionRepository productTransactionRepository;
    private final DebitRepository debitRepository;
    private final UserSession userSession;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Long makeProdTransaction(ProductTransaction productTransaction, List<Product> products) {
        long yattId = userSession.yattId();

        // 1. Products link + totalPrice — bitta loop (stream'dan tezroq)
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Product product : products) {
            product.calculateTotalPrice();
            product.setProductTransaction(productTransaction);
            product.setYattId(yattId);
            grandTotal = grandTotal.add(product.getTotalPrice());
        }

        // 2. Transaction saqlash
        productTransaction.setTotalPrice(grandTotal);
        ProductTransaction savedTx = productTransactionRepository.save(productTransaction);

        // 3. Products saqlash — savedTx.getId() kerak emas, reference allaqachon set qilingan
        productRepository.saveAll(products);

        // 4. Debt — fromUser objecti bor, ID uchun query yo'q
        createOrUpdateDebt(savedTx.getFromUser(), yattId, grandTotal);

        return savedTx.getId();
    }

    private void createOrUpdateDebt(AuthUser fromUser, long yattId, BigDecimal amount) {
        int updated = debitRepository.incrementActiveAmount(fromUser.getId(), yattId, amount);
        if (updated == 0) {
            // Faqat birinchi marta — yangi debt
            debitRepository.save(Debt.builder()
                    .activeAmount(amount)
                    .fromUser(fromUser)
                    .yattId(yattId)
                    .build());
        }
    }

    @Override
    @Transactional
    public void updateProductTransaction(ProductTransaction productTransaction,
                                         ProductTransactionUpdate request) throws AccessDeniedException {

        BigDecimal oldTotal = productTransaction.getTotalPrice();

        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            List<Product> existingProducts =
                    productRepository.getProductByProductTransaction(productTransaction.getId(), userSession.yattId());

            // Existing productlarni map qilib olish — ID bo'yicha
            Map<Long, Product> existingMap = existingProducts.stream()
                    .filter(p -> p.getId() != null)
                    .collect(Collectors.toMap(Product::getId, p -> p));

            for (ProductUpdateRequest req : request.getProducts()) {

                if (req.getId() != null && existingMap.containsKey(req.getId())) {
                    // UPDATE
                    Product existing = existingMap.get(req.getId());

                    if (req.getPrice() != null)
                        existing.setPrice(req.getPrice());
                    if (req.getQuantity() != null)
                        existing.setQuantity(req.getQuantity());
                    if (req.getName() != null)
                        existing.setName(req.getName());

                    existing.calculateTotalPrice();

                } else {
                    // 🆕 CREATE (sizda yo‘q edi)
                    Product newProduct = new Product();
                    newProduct.setName(req.getName());
                    newProduct.setPrice(req.getPrice());
                    newProduct.setQuantity(req.getQuantity());
                    newProduct.setProductTransaction(productTransaction);
                    newProduct.setYattId(userSession.yattId());

                    newProduct.calculateTotalPrice();

                    productRepository.save(newProduct);
                    existingProducts.add(newProduct); // 🔥 MUHIM
                }
            }

            // Grand total qayta hisoblash
            BigDecimal newTotal = existingProducts.stream()
                    .map(Product::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            productTransaction.setTotalPrice(newTotal);

            if (request.getTransactionDate() != null)
                productTransaction.setTransactionDate(request.getTransactionDate());
            if (request.getIsCompleted() != null) {
                if (!productTransaction.getToUser().getId().equals(userSession.userId())) {
                    throw new AccessDeniedException("you.cant.update.isCompleted");
                }
                productTransaction.setIsCompleted(request.getIsCompleted());
            }
            productTransactionRepository.save(productTransaction);

            // Debt farqini yangilash
            BigDecimal diff = newTotal.subtract(oldTotal);
            if (diff.compareTo(BigDecimal.ZERO) != 0) {
                adjustDebt(productTransaction.getFromUser().getId(),
                        userSession.yattId(), diff);
            }
        }
    }


    private void adjustDebt(Long userId, long yattId, BigDecimal diff) {
        debitRepository.findByUserId(userId, yattId).ifPresent(debt -> {
            debt.setActiveAmount(debt.getActiveAmount().add(diff));
            debitRepository.save(debt);
        });
    }
}

