package com.company.kassa.service.small_seller;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.kassa.KassaCreateRequest;
import com.company.kassa.dto.kassa.KassaFilter;
import com.company.kassa.dto.kassa.KassaResponse;
import com.company.kassa.dto.kassa.KassaUpdateRequest;
import com.company.kassa.dto.money.MoneyTransactionFilter;
import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.dto.money.MoneyTransactionUpdate;
import com.company.kassa.dto.product.ProductTransactionFilter;
import com.company.kassa.dto.product.ProductTransactionRequest;
import com.company.kassa.dto.product.ProductTransactionResponse;
import com.company.kassa.dto.product.ProductTransactionUpdate;
import com.company.kassa.dto.user.UserPasswordUpdate;
import com.company.kassa.dto.user.UserResponse;
import com.company.kassa.dto.user.UserUpdateRequest;
import com.company.kassa.models.enums.MoneyType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

@Service
public interface SmallSellerService {
    HttpApiResponse<Long> makeProductTransaction(Long fromUserId, Object o, LocalDate transactionDate, List<ProductTransactionRequest.ProductRequest> products);

    HttpApiResponse<Long> makeMoneyTransaction(Object o, Long toUserId, BigDecimal amount, LocalDate transactionDate, MoneyType moneyType);

    HttpApiResponse<Long> kassaCreate(KassaCreateRequest request);

    HttpApiResponse<Page<KassaResponse>> getKassaFilter(KassaFilter kassaFilter, Pageable pageable);

    byte[] getKassaFilterExcel(KassaFilter kassaFilter) throws IOException;

    HttpApiResponse<Page<ProductTransactionResponse>> getProductTransactions(ProductTransactionFilter productTransactionFilter, Pageable pageable);

    byte[] getProductTransactionExcel(ProductTransactionFilter productTransactionFilter) throws IOException;

    HttpApiResponse<Page<MoneyTransactionResponse>> getMoneyTransactions(MoneyTransactionFilter moneyTransactionFilter, Pageable pageable);

    byte[] getMoneyTransactionExcel(MoneyTransactionFilter moneyTransactionFilter) throws IOException;

    HttpApiResponse<Boolean> updateProfile(UserUpdateRequest request);

    HttpApiResponse<Boolean> updateProfilePassword(UserPasswordUpdate request);

    HttpApiResponse<List<UserResponse>> getAllUsersInYatt();

    HttpApiResponse<MoneyTransactionResponse> getMoneyTransactionById(Long id);

    HttpApiResponse<ProductTransactionResponse> getProductTransactionById(Long id);

    HttpApiResponse<KassaResponse> getKassaById(Long id);

    HttpApiResponse<Long> updateProductTransaction(ProductTransactionUpdate request, Long id) throws AccessDeniedException;

    HttpApiResponse<Long> updateMoneyTransaction(MoneyTransactionUpdate request, Long id) throws AccessDeniedException;

    HttpApiResponse<Long> updateKassaById(KassaUpdateRequest request, Long id);
}
