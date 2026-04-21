package com.company.kassa.service.yattAdmin;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.debit.DebitFilter;
import com.company.kassa.dto.debit.DebitResponse;
import com.company.kassa.dto.kassa.KassaCreateRequest;
import com.company.kassa.dto.kassa.KassaFilter;
import com.company.kassa.dto.kassa.KassaResponse;
import com.company.kassa.dto.money.MoneyTransactionFilter;
import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.dto.product.ProductTransactionFilter;
import com.company.kassa.dto.product.ProductTransactionRequest;
import com.company.kassa.dto.product.ProductTransactionResponse;
import com.company.kassa.dto.user.UserCreateRequest;
import com.company.kassa.dto.user.UserPasswordUpdate;
import com.company.kassa.dto.user.UserResponse;
import com.company.kassa.dto.user.UserUpdateRequest;
import com.company.kassa.models.enums.MoneyType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public interface YattAdminService {
    HttpApiResponse<Long> addStaff(UserCreateRequest request);

    HttpApiResponse<Long> makeProductTransaction(Long fromUserId, Long toUserId, LocalDate transactionDate, List<ProductTransactionRequest.ProductRequest> products);

    HttpApiResponse<Long> makeMoneyTransaction(Long fromUserId, Long toUserId, BigDecimal amount, LocalDate transactionDate, MoneyType moneyType);

    HttpApiResponse<Long> kassaCreate(@Valid KassaCreateRequest request);

    HttpApiResponse<Page<KassaResponse>> getKassaFilter(KassaFilter kassaFilter, Pageable pageable);

    byte[] getKassaFilterExcel(KassaFilter kassaFilter);

    HttpApiResponse<Page<ProductTransactionResponse>> getProductTransactions(ProductTransactionFilter productTransactionFilter, Pageable pageable);

    byte[] getProductTransactionExcel(ProductTransactionFilter productTransactionFilter);

    HttpApiResponse<Page<MoneyTransactionResponse>> getMoneyTransactions(MoneyTransactionFilter moneyTransactionFilter, Pageable pageable);

    byte[] getMoneyTransactionExcel(MoneyTransactionFilter moneyTransactionFilter);

    HttpApiResponse<List<UserResponse>> getAllUsersInYatt();

    HttpApiResponse<Boolean> updateProfile(UserUpdateRequest request);

    HttpApiResponse<Boolean> updateProfilePassword(UserPasswordUpdate request);

    HttpApiResponse<Page<DebitResponse>> getDebits(DebitFilter filter, Pageable of);

    byte[] getDebitsExcel(DebitFilter filter);
}
