package com.company.kassa.service.small_seller.impl;

import com.company.kassa.config.security.UserSession;
import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.kassa.KassaCreateRequest;
import com.company.kassa.dto.kassa.KassaFilter;
import com.company.kassa.dto.kassa.KassaResponse;
import com.company.kassa.dto.kassa.KassaUpdateRequest;
import com.company.kassa.dto.money.MoneyTransactionFilter;
import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.dto.money.MoneyTransactionUpdate;
import com.company.kassa.dto.product.*;
import com.company.kassa.dto.user.UserPasswordUpdate;
import com.company.kassa.dto.user.UserProfileResponse;
import com.company.kassa.dto.user.UserResponse;
import com.company.kassa.dto.user.UserUpdateRequest;
import com.company.kassa.models.*;
import com.company.kassa.models.enums.MoneyType;
import com.company.kassa.repository.*;
import com.company.kassa.repository.specification.KassaSpecification;
import com.company.kassa.repository.specification.MoneyTransactionSpecification;
import com.company.kassa.repository.specification.ProductTransactionSpecification;
import com.company.kassa.service.auth.mapper.AuthMapper;
import com.company.kassa.service.kassa.KassaMapper;
import com.company.kassa.service.money.MoneyTransactionService;
import com.company.kassa.service.money.mapper.MoneyTransactionMapper;
import com.company.kassa.service.product.ProductTransactionService;
import com.company.kassa.service.product.mapper.ProductMapper;
import com.company.kassa.service.product.mapper.ProductTransactionMapper;
import com.company.kassa.service.small_seller.SmallSellerService;
import com.company.kassa.utils.ExcelUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SmallSellerServiceImpl implements SmallSellerService {
    private final ProductTransactionService productTransactionService;
    private final MoneyTransactionService moneyTransactionService;
    private final UserSession userSession;
    private final AuthUserRepository authUserRepository;
    private final KassaRepository kassaRepository;
    private final ProductMapper productMapper;
    private final KassaMapper kassaMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProductTransactionRepository productTransactionRepository;
    private final ProductTransactionMapper productTransactionMapper;
    private final ProductRepository productRepository;
    private final MoneyTransactionRepository moneyTransactionRepository;
    private final MoneyTransactionMapper moneyTransactionMapper;


    @Transactional
    @Override
    public HttpApiResponse<Long> makeProductTransaction(Long fromUserId, Object o, LocalDate transactionDate, List<ProductTransactionRequest.ProductRequest> products) {
        AuthUser fromUser = authUserRepository.findByIdAndYattIdAndDeletedAtIsNull(fromUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        ProductTransaction productTransaction = ProductTransaction.builder()
                .fromUser(fromUser)
                .toUser(userSession.getCurrentUser())
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

    @Transactional
    @Override
    public HttpApiResponse<Long> makeMoneyTransaction(Object o, Long toUserId, BigDecimal amount, LocalDate transactionDate, MoneyType moneyType) {
        AuthUser toUser = authUserRepository.findByIdAndYattIdAndDeletedAtIsNull(toUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        MoneyTransaction transaction = MoneyTransaction.builder()
                .fromUser(userSession.getCurrentUser())
                .toUser(toUser)
                .amount(amount)
                .transactionDate(transactionDate)
                .moneyType(moneyType)
                .yattId(userSession.yattId())
                .build();

        Long id = moneyTransactionService.makeMoneyTransaction(transaction);
        return HttpApiResponse.<Long>builder()
                .success(true)
                .status(201)
                .message("ok")
                .data(id)
                .build();
    }

    @Transactional
    @Override
    public HttpApiResponse<Long> kassaCreate(KassaCreateRequest request) {
        Kassa kassa = kassaMapper.toEntity(request);
        BigDecimal totalAmount =
                kassa.getCard().add(kassa.getCash()).add(kassa.getTerminal()); // we can optimize
        kassa.setYattId(userSession.yattId());
        kassa.setOwner(userSession.getCurrentUser());
        kassa.setTotaAmount(totalAmount);

        Kassa saved = kassaRepository.save(kassa);

        return HttpApiResponse.<Long>builder()
                .success(true)
                .status(201)
                .message("ok")
                .data(saved.getId())
                .build();
    }

    @Transactional(readOnly = true)
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
    public HttpApiResponse<List<UserResponse>> getAllUsersInYatt() {
        List<AuthUser> users = authUserRepository.findAllByYattId(userSession.yattId());

        return HttpApiResponse.<List<UserResponse>>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(users.stream().map(authMapper::mapToUserResponse).toList())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<Page<KassaResponse>> getKassaFilter(KassaFilter kassaFilter, Pageable pageable) {
        kassaFilter.setOwnerId(userSession.userId());
        var spec = new KassaSpecification(kassaFilter);
        Page<Kassa> kassaPage = kassaRepository.findAll(spec, pageable);

        return HttpApiResponse.<Page<KassaResponse>>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(kassaPage.map(kassaMapper::mapToResponse))
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] getKassaFilterExcel(KassaFilter kassaFilter) throws IOException {
        var spec = new KassaSpecification(kassaFilter);
        List<Kassa> kassaList = kassaRepository.findAll(spec);

        return ExcelUtil.generateKassaExcel(kassaList);
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<Page<ProductTransactionResponse>> getProductTransactions(
            ProductTransactionFilter filter, Pageable pageable) {
        filter.setToUserId(userSession.userId());
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
        filter.setToUserId(userSession.userId());
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
        moneyTransactionFilter.setFromUserId(userSession.userId());
        var spec = new MoneyTransactionSpecification(moneyTransactionFilter);
        Page<MoneyTransaction> transactionPage = moneyTransactionRepository.findAll(spec, pageable);

        return HttpApiResponse.<Page<MoneyTransactionResponse>>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(transactionPage.map(moneyTransactionMapper::mapToRes))
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] getMoneyTransactionExcel(MoneyTransactionFilter moneyTransactionFilter) throws IOException {
        moneyTransactionFilter.setFromUserId(userSession.userId());
        var spec = new MoneyTransactionSpecification(moneyTransactionFilter);
        List<MoneyTransaction> transactions = moneyTransactionRepository.findAll(spec);

        return ExcelUtil.generateMoneyTransactionFile(transactions);
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<MoneyTransactionResponse> getMoneyTransactionById(Long id) {
        MoneyTransaction transaction = moneyTransactionRepository.findByIdAndYattId(id, userSession.yattId())
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
        ProductTransaction productTransaction = productTransactionRepository.findByIdAndYattId(id, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("product.transaction.not.found"));

        ProductTransactionResponse response = productTransactionMapper.mapToRes(productTransaction);

        return HttpApiResponse.<ProductTransactionResponse>builder()
                .status(200)
                .success(true)
                .message("ok")
                .data(response)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<KassaResponse> getKassaById(Long id) {
        Kassa kassa = kassaRepository.findByIdAndYattId(id, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("kassa.not.found"));
        KassaResponse response = kassaMapper.mapToResponse(kassa);

        return HttpApiResponse.<KassaResponse>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(response)
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

    @Override
    public HttpApiResponse<Long> updateKassaById(KassaUpdateRequest request, Long id) {
        return null;
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
}
