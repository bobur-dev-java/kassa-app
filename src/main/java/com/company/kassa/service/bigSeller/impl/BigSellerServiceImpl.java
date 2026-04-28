package com.company.kassa.service.bigSeller.impl;

import com.company.kassa.config.security.UserSession;
import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.debit.DebitResponse;
import com.company.kassa.dto.money.MoneyTransactionFilter;
import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.dto.money.MoneyTransactionUpdate;
import com.company.kassa.dto.product.*;
import com.company.kassa.dto.user.UserPasswordUpdate;
import com.company.kassa.dto.user.UserProfileResponse;
import com.company.kassa.dto.user.UserResponse;
import com.company.kassa.dto.user.UserUpdateRequest;
import com.company.kassa.models.*;
import com.company.kassa.repository.AuthUserRepository;
import com.company.kassa.repository.MoneyTransactionRepository;
import com.company.kassa.repository.ProductRepository;
import com.company.kassa.repository.ProductTransactionRepository;
import com.company.kassa.repository.specification.DebtSpecification;
import com.company.kassa.repository.specification.MoneyTransactionSpecification;
import com.company.kassa.repository.specification.ProductTransactionSpecification;
import com.company.kassa.service.auth.mapper.AuthMapper;
import com.company.kassa.service.bigSeller.BigSellerService;
import com.company.kassa.service.money.MoneyTransactionService;
import com.company.kassa.service.money.mapper.MoneyTransactionMapper;
import com.company.kassa.service.product.ProductTransactionService;
import com.company.kassa.service.product.mapper.ProductMapper;
import com.company.kassa.service.product.mapper.ProductTransactionMapper;
import com.company.kassa.utils.ExcelUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BigSellerServiceImpl implements BigSellerService {

    private final UserSession userSession;
    private final AuthUserRepository authUserRepository;
    private final ProductTransactionService productTransactionService;
    private final ProductMapper productMapper;
    private final AuthMapper authMapper;
    private final ProductTransactionMapper productTransactionMapper;
    private final ProductRepository productRepository;
    private final ProductTransactionRepository productTransactionRepository;
    private final MoneyTransactionRepository moneyTransactionRepository;
    private final MoneyTransactionMapper moneyTransactionMapper;
    private final MoneyTransactionService moneyTransactionService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public HttpApiResponse<Long> makeProductTransaction(Object o, Long toUserId, LocalDate transactionDate, List<ProductTransactionRequest.ProductRequest> products) {
        AuthUser toUser = authUserRepository.findByIdAndYattIdAndDeletedAtIsNull(toUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        ProductTransaction productTransaction = ProductTransaction.builder()
                .fromUser(userSession.getCurrentUser())
                .toUser(toUser)
                .transactionDate(transactionDate)
                .yattId(userSession.yattId())
                .build();

        Long id = productTransactionService.makeProdTransaction(productTransaction,
                products.stream().map(productMapper::toEntity).toList());

        return HttpApiResponse.<Long>builder()
                .success(true)
                .status(201)
                .message("ok")
                .data(id)
                .build();
    }

    @Override
    public HttpApiResponse<UserProfileResponse> getProfile() {
        AuthUser user = userSession.getCurrentUser();

        UserProfileResponse response = UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName() != null ? user.getFullName() : null)
                .username(user.getUsername())
                .role(userSession.getCurrentUserRole() != null ? userSession.getCurrentUserRole().name() : null)
                .build();

        return HttpApiResponse.<UserProfileResponse>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(response)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<MoneyTransactionResponse> getMoneyTransactionById(Long id) {
        MoneyTransaction transaction = moneyTransactionRepository.findByIdAndYattIdAndToUserId(id, userSession.yattId(), userSession.userId())
                .orElseThrow(() -> new EntityNotFoundException("money.transaction.not.found"));

        MoneyTransactionResponse response = moneyTransactionMapper.mapToRes(transaction);

        return HttpApiResponse.<MoneyTransactionResponse>builder()
                .status(200)
                .success(true)
                .message("ok")
                .data(response)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<ProductTransactionResponse> getProductTransactionById(Long id) {
        ProductTransaction productTransaction = productTransactionRepository.findByIdAndYattIdAndFromUserId(id, userSession.yattId(), userSession.userId())
                .orElseThrow(() -> new EntityNotFoundException("product.transaction.not.found"));

        List<Product> products = productRepository.findAllByProductTransactionId(id, userSession.yattId());

        ProductTransactionResponse response = productTransactionMapper.mapToRes(productTransaction);
        response.setProducts(products.stream().map(productMapper::mapToResponse).toList());

        return HttpApiResponse.<ProductTransactionResponse>builder()
                .status(200)
                .success(true)
                .message("ok")
                .data(response)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<Page<ProductTransactionResponse>> getProductTransactions(ProductTransactionFilter filter, Pageable pageable) {
        filter.setFromUserId(userSession.userId());
        var spec = new ProductTransactionSpecification(filter);

        Page<ProductTransaction> transactionPage =
                productTransactionRepository.findAll(spec, pageable);

        List<Long> transactionIds = transactionPage.stream()
                .map(ProductTransaction::getId)
                .toList();

        List<Product> products = productRepository.findAllByProductTransactionIdIn(transactionIds);

        Map<Long, List<ProductResponse>> productMap = products.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getProductTransaction().getId(),
                        Collectors.mapping(productMapper::mapToResponse, Collectors.toList())
                ));

        Page<ProductTransactionResponse> responses = transactionPage.map(transaction -> {
            ProductTransactionResponse res = productTransactionMapper.mapToRes(transaction);

            res.setProducts(
                    productMap.getOrDefault(transaction.getId(), Collections.emptyList())
            );

            return res;
        });

        return HttpApiResponse.<Page<ProductTransactionResponse>>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(responses)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] getProductTransactionExcel(ProductTransactionFilter filter) throws IOException {
        filter.setFromUserId(userSession.userId());
        var spec = new ProductTransactionSpecification(filter);

        List<ProductTransaction> transactionList =
                productTransactionRepository.findAll(spec);

        List<Long> transactionIds = transactionList.stream()
                .map(ProductTransaction::getId)
                .toList();

        List<Product> products = productRepository.findAllByProductTransactionIdIn(transactionIds);

        Map<Long, List<ProductResponse>> productMap = products.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getProductTransaction().getId(),
                        Collectors.mapping(productMapper::mapToResponse, Collectors.toList())
                ));

        List<ProductTransactionExcel> responses = transactionList.stream().map(transaction -> {
            ProductTransactionExcel res = productTransactionMapper.mapToResExcel(transaction);

            res.setProducts(
                    productMap.getOrDefault(transaction.getId(), Collections.emptyList())
            );

            return res;
        }).toList();

        return ExcelUtil.generateProductTransactionHistoryExcel(responses);
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<Page<MoneyTransactionResponse>> getMoneyTransactions(MoneyTransactionFilter moneyTransactionFilter, Pageable pageable) {
        moneyTransactionFilter.setToUserId(userSession.userId());
        var spec = new MoneyTransactionSpecification(moneyTransactionFilter);
        Page<MoneyTransaction> transactionPage = moneyTransactionRepository.findAll(spec, pageable);

        return HttpApiResponse.<Page<MoneyTransactionResponse>>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(transactionPage.map(moneyTransactionMapper::mapToRes))
                .build();
    }

    @Override
    public byte[] getMoneyTransactionExcel(MoneyTransactionFilter moneyTransactionFilter) throws IOException {
        moneyTransactionFilter.setToUserId(userSession.userId());
        var spec = new MoneyTransactionSpecification(moneyTransactionFilter);
        List<MoneyTransaction> transactions = moneyTransactionRepository.findAll(spec);

        return ExcelUtil.generateMoneyTransactionFile(transactions);
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<List<UserResponse>> getAllUsersInYatt() {
        List<AuthUser> users = authUserRepository.findAllByYattId(userSession.yattId());

        return HttpApiResponse.<List<UserResponse>>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(users.stream().map(authMapper::mapToUserResponse).toList())
                .build();
    }

    @Transactional
    @Override
    public HttpApiResponse<Long> updateProductTransaction(ProductTransactionUpdate request, Long id) throws AccessDeniedException {
        ProductTransaction productTransaction = productTransactionRepository.findByIdAndYattId(id, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("product.transaction.not.found"));
        productTransactionService.updateProductTransaction(productTransaction, request);

        return HttpApiResponse.<Long>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(productTransaction.getId())
                .build();
    }

    @Transactional
    @Override
    public HttpApiResponse<Long> updateMoneyTransaction(MoneyTransactionUpdate request, Long id) throws AccessDeniedException {
        MoneyTransaction transaction = moneyTransactionRepository.findByIdAndYattId(id, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("money.transaction.not.found"));


        moneyTransactionService.updateMoneyTransaction(transaction, request);

        return HttpApiResponse.<Long>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(transaction.getId())
                .build();
    }

    @Transactional
    @Override
    public HttpApiResponse<Boolean> updateProfile(UserUpdateRequest request) {
        if (request.getUsername() != null) {
            boolean exists = authUserRepository.existsByUsername(request.getUsername(), userSession.yattId());
            if (exists) {
                throw new IllegalArgumentException("username.already.exists");
            }
        }
        authMapper.updateEntity(userSession.getCurrentUser(), request);

        return HttpApiResponse.<Boolean>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(true)
                .build();
    }

    @Transactional
    @Override
    public HttpApiResponse<Boolean> updateProfilePassword(UserPasswordUpdate request) {
        AuthUser user = userSession.getCurrentUser();
        boolean matches = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if (!matches) {
            throw new IllegalArgumentException("password.incorrect");
        }
        if (Objects.equals(request.getNewPassword(), request.getOldPassword())) {
            throw new IllegalArgumentException("new.password.must.be.different.from.old");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        return HttpApiResponse.<Boolean>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(true)
                .build();
    }

    @Override
    public HttpApiResponse<Page<DebitResponse>> getDebits(Pageable pageable) {
        return null;
    }

    @Override
    public byte[] getDebitsExcel() {
        return new byte[0];
    }
}
