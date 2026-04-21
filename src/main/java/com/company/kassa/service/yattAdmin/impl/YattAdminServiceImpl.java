package com.company.kassa.service.yattAdmin.impl;

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
import com.company.kassa.service.yattAdmin.YattAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class YattAdminServiceImpl implements YattAdminService {

    @Override
    public HttpApiResponse<Long> addStaff(UserCreateRequest request) {
        return null;
    }

    @Override
    public HttpApiResponse<Long> makeProductTransaction(Long fromUserId, Long toUserId, LocalDate transactionDate, List<ProductTransactionRequest.ProductRequest> products) {
        return null;
    }

    @Override
    public HttpApiResponse<Long> makeMoneyTransaction(Long fromUserId, Long toUserId, BigDecimal amount, LocalDate transactionDate, MoneyType moneyType) {
        return null;
    }

    @Override
    public HttpApiResponse<Long> kassaCreate(KassaCreateRequest request) {
        return null;
    }

    @Override
    public HttpApiResponse<Page<KassaResponse>> getKassaFilter(KassaFilter kassaFilter, Pageable pageable) {
        return null;
    }

    @Override
    public byte[] getKassaFilterExcel(KassaFilter kassaFilter) {
        return new byte[0];
    }

    @Override
    public HttpApiResponse<Page<ProductTransactionResponse>> getProductTransactions(ProductTransactionFilter productTransactionFilter, Pageable pageable) {
        return null;
    }

    @Override
    public byte[] getProductTransactionExcel(ProductTransactionFilter productTransactionFilter) {
        return new byte[0];
    }

    @Override
    public HttpApiResponse<Page<MoneyTransactionResponse>> getMoneyTransactions(MoneyTransactionFilter moneyTransactionFilter, Pageable pageable) {
        return null;
    }

    @Override
    public byte[] getMoneyTransactionExcel(MoneyTransactionFilter moneyTransactionFilter) {
        return new byte[0];
    }

    @Override
    public HttpApiResponse<List<UserResponse>> getAllUsersInYatt() {
        return null;
    }

    @Override
    public HttpApiResponse<Boolean> updateProfile(UserUpdateRequest request) {
        return null;
    }

    @Override
    public HttpApiResponse<Boolean> updateProfilePassword(UserPasswordUpdate request) {
        return null;
    }

    @Override
    public HttpApiResponse<Page<DebitResponse>> getDebits(DebitFilter filter, Pageable of) {
        return null;
    }

    @Override
    public byte[] getDebitsExcel(DebitFilter filter) {
        return new byte[0];
    }
}
