package com.company.kassa.service.bigSeller;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.debit.DebitResponse;
import com.company.kassa.dto.money.MoneyTransactionFilter;
import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.dto.money.MoneyTransactionUpdate;
import com.company.kassa.dto.product.ProductTransactionFilter;
import com.company.kassa.dto.product.ProductTransactionRequest;
import com.company.kassa.dto.product.ProductTransactionResponse;
import com.company.kassa.dto.product.ProductTransactionUpdate;
import com.company.kassa.dto.user.UserPasswordUpdate;
import com.company.kassa.dto.user.UserProfileResponse;
import com.company.kassa.dto.user.UserResponse;
import com.company.kassa.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

@Service
public interface BigSellerService {
    HttpApiResponse<Long> makeProductTransaction(Object o, Long toUserId, LocalDate transactionDate, List<ProductTransactionRequest.ProductRequest> products);

    HttpApiResponse<UserProfileResponse> getProfile();

    HttpApiResponse<MoneyTransactionResponse> getMoneyTransactionById(Long id);

    HttpApiResponse<ProductTransactionResponse> getProductTransactionById(Long id);

    HttpApiResponse<Page<ProductTransactionResponse>> getProductTransactions(ProductTransactionFilter productTransactionFilter, Pageable pageable);

    byte[] getProductTransactionExcel(ProductTransactionFilter productTransactionFilter) throws IOException;

    HttpApiResponse<Page<MoneyTransactionResponse>> getMoneyTransactions(MoneyTransactionFilter moneyTransactionFilter, Pageable pageable);

    byte[] getMoneyTransactionExcel(MoneyTransactionFilter moneyTransactionFilter) throws IOException;

    HttpApiResponse<List<UserResponse>> getAllUsersInYatt();

    HttpApiResponse<Long> updateProductTransaction(ProductTransactionUpdate request, Long id) throws AccessDeniedException;

    HttpApiResponse<Long> updateMoneyTransaction(MoneyTransactionUpdate request, Long id) throws AccessDeniedException;

    HttpApiResponse<Boolean> updateProfile(UserUpdateRequest request);

    HttpApiResponse<Boolean> updateProfilePassword(UserPasswordUpdate request);

    HttpApiResponse<Page<DebitResponse>> getDebits(Pageable pageable);

    byte[] getDebitsExcel();
}
