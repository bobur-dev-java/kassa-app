package com.company.kassa.service.yattAdmin.impl;

import com.company.kassa.config.security.UserSession;
import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.debit.DebitFilter;
import com.company.kassa.dto.debit.DebitResponse;
import com.company.kassa.dto.kassa.KassaCreateRequest;
import com.company.kassa.dto.kassa.KassaFilter;
import com.company.kassa.dto.kassa.KassaResponse;
import com.company.kassa.dto.money.MoneyTransactionFilter;
import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.dto.product.*;
import com.company.kassa.dto.user.*;
import com.company.kassa.models.*;
import com.company.kassa.models.enums.MoneyType;
import com.company.kassa.models.enums.YaTTUserRole;
import com.company.kassa.repository.*;
import com.company.kassa.repository.specification.DebtSpecification;
import com.company.kassa.repository.specification.KassaSpecification;
import com.company.kassa.repository.specification.MoneyTransactionSpecification;
import com.company.kassa.repository.specification.ProductTransactionSpecification;
import com.company.kassa.service.auth.mapper.AuthMapper;
import com.company.kassa.service.debt.mapper.DebtMapper;
import com.company.kassa.service.kassa.KassaMapper;
import com.company.kassa.service.money.MoneyTransactionService;
import com.company.kassa.service.money.mapper.MoneyTransactionMapper;
import com.company.kassa.service.product.ProductTransactionService;
import com.company.kassa.service.product.mapper.ProductMapper;
import com.company.kassa.service.product.mapper.ProductTransactionMapper;
import com.company.kassa.service.yattAdmin.YattAdminService;
import com.company.kassa.utils.ExcelUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class YattAdminServiceImpl implements YattAdminService {

    private final AuthMapper authMapper;
    private final UserSession userSession;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserRepository authUserRepository;
    private final ProductTransactionService productTransactionService;
    private final ProductMapper productMapper;
    private final MoneyTransactionService moneyTransactionService;
    private final KassaRepository kassaRepository;
    private final KassaMapper kassaMapper;
    private final ProductTransactionMapper productTransactionMapper;
    private final ProductRepository productRepository;
    private final ProductTransactionRepository productTransactionRepository;
    private final MoneyTransactionRepository moneyTransactionRepository;
    private final MoneyTransactionMapper moneyTransactionMapper;
    private final DebitRepository debitRepository;
    private final DebtMapper debtMapper;

    @Transactional
    @Override
    public HttpApiResponse<Long> addStaff(UserCreateRequest request) {
        if (request.getRole() == YaTTUserRole.ADMIN)
            throw new AccessDeniedException("you.can.not.create.admin");
        AuthUser entity = authMapper.toEntity(request);
        entity.setYattId(userSession.yattId());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));

        AuthUser saved = authUserRepository.save(entity);

        return HttpApiResponse.<Long>builder()
                .success(true)
                .status(201)
                .message("ok")
                .data(saved.getId())
                .build();
    }

    @Transactional
    @Override
    public HttpApiResponse<Long> makeProductTransaction(Long fromUserId, Long toUserId, LocalDate transactionDate,
                                                        List<ProductTransactionRequest.ProductRequest> products) {
        AuthUser fromUser = authUserRepository.findByIdAndYattIdAndDeletedAtIsNull(fromUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        AuthUser toUserUser = authUserRepository.findByIdAndYattIdAndDeletedAtIsNull(toUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        ProductTransaction productTransaction = ProductTransaction.builder()
                .fromUser(fromUser)
                .toUser(toUserUser)
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
    public HttpApiResponse<Long> makeMoneyTransaction(Long fromUserId, Long toUserId, BigDecimal amount, LocalDate transactionDate, MoneyType moneyType) {
        AuthUser toUser = authUserRepository.findByIdAndYattIdAndDeletedAtIsNull(toUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        AuthUser fromUser = authUserRepository.findByIdAndYattIdAndDeletedAtIsNull(fromUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("user.not.found"));

        MoneyTransaction transaction = MoneyTransaction.builder()
                .fromUser(fromUser)
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
    public HttpApiResponse<Page<KassaResponse>> getKassaFilter(KassaFilter kassaFilter, Pageable pageable) {
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
    public HttpApiResponse<Page<ProductTransactionResponse>> getProductTransactions(ProductTransactionFilter filter, Pageable pageable) {
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

    @Transactional(readOnly = true)
    @Override
    public HttpApiResponse<Page<DebitResponse>> getDebits(DebitFilter filter, Pageable pageable) {
        var spec = new DebtSpecification(filter);
        Page<Debt> debtPage = debitRepository.findAll(spec, pageable);

        return HttpApiResponse.<Page<DebitResponse>>builder()
                .success(true)
                .status(200)
                .message("ok")
                .data(debtPage.map(debtMapper::mapToRes))
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] getDebitsExcel(DebitFilter filter) throws IOException {
        var spec = new DebtSpecification(filter);
        List<Debt> debtList = debitRepository.findAll(spec);

        return ExcelUtil.generateDebtExcel(debtList);
    }
}
