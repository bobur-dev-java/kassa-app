package com.company.kassa.controller;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.debit.DebitFilter;
import com.company.kassa.dto.debit.DebitResponse;
import com.company.kassa.dto.kassa.KassaCreateRequest;
import com.company.kassa.dto.kassa.KassaFilter;
import com.company.kassa.dto.kassa.KassaResponse;
import com.company.kassa.dto.money.MoneyTransactionFilter;
import com.company.kassa.dto.money.MoneyTransactionRequest;
import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.dto.product.ProductTransactionFilter;
import com.company.kassa.dto.product.ProductTransactionRequest;
import com.company.kassa.dto.product.ProductTransactionResponse;
import com.company.kassa.dto.user.*;
import com.company.kassa.service.yattAdmin.YattAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/yatt-admin")
public class YattAdminController {
    private final YattAdminService yattAdminService;

    private static final String EXCEL_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");


    @PostMapping("/add-staff")
    public ResponseEntity<HttpApiResponse<Long>> addStaff(@Valid @RequestBody UserCreateRequest request) {
        HttpApiResponse<Long> response = yattAdminService.addStaff(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/product-transaction")
    public ResponseEntity<HttpApiResponse<Long>> makeProductTransaction(@Valid @RequestBody ProductTransactionRequest request) {
        HttpApiResponse<Long> response =
                yattAdminService.makeProductTransaction(
                        request.getFromUserId(), request.getToUserId(), request.getTransactionDate(), request.getProducts());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/money-transaction")
    public ResponseEntity<HttpApiResponse<Long>> makeMoneyTransaction(@Valid @RequestBody MoneyTransactionRequest request) {
        HttpApiResponse<Long> response =
                yattAdminService.makeMoneyTransaction(request.getFromUserId(), request.getToUserId(),
                        request.getAmount(), request.getTransactionDate(), request.getMoneyType());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/kassa")
    public ResponseEntity<HttpApiResponse<Long>> kassaCreate(@Valid @RequestBody KassaCreateRequest request) {
        HttpApiResponse<Long> response = yattAdminService.kassaCreate(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpApiResponse<UserProfileResponse>> getProfile() {
        HttpApiResponse<UserProfileResponse> response = yattAdminService.getProfile();
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @GetMapping("/kassa")
    public ResponseEntity<HttpApiResponse<Page<KassaResponse>>> getKassaFilter(
            @ModelAttribute KassaFilter kassaFilter,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        HttpApiResponse<Page<KassaResponse>> response =
                yattAdminService.getKassaFilter(new KassaFilter(kassaFilter.getOwnerId(), kassaFilter.getFrom(), kassaFilter.getTo(), kassaFilter.getIsCompleted()), PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/kassa-excel")
    public ResponseEntity<byte[]> getKassaFilterExcel(@ModelAttribute KassaFilter kassaFilter) throws IOException {
        byte[] response =
                yattAdminService.getKassaFilterExcel(new KassaFilter(
                        kassaFilter.getOwnerId(), kassaFilter.getFrom(), kassaFilter.getTo(), kassaFilter.getIsCompleted()));
        String filename = "kassa_" + OffsetDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(EXCEL_CONTENT_TYPE))
                .body(response);
    }

    @GetMapping("/product-transactions")
    public ResponseEntity<HttpApiResponse<Page<ProductTransactionResponse>>> getProductTransactions(
            @ModelAttribute ProductTransactionFilter filter,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        HttpApiResponse<Page<ProductTransactionResponse>> response =
                yattAdminService.getProductTransactions(new ProductTransactionFilter(
                        filter.getFromUserId(), filter.getToUserId(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()), PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/product-transactions/excel")
    public ResponseEntity<byte[]> getProductTransactionExcel(@ModelAttribute ProductTransactionFilter filter) throws IOException {
        byte[] response =
                yattAdminService.getProductTransactionExcel(new ProductTransactionFilter(
                        filter.getFromUserId(), filter.getToUserId(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()));

        String filename = "product_transactions_" + OffsetDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(EXCEL_CONTENT_TYPE))
                .body(response);
    }

    @GetMapping("/money-transactions")
    public ResponseEntity<HttpApiResponse<Page<MoneyTransactionResponse>>> getMoneyTransactions(
            @ModelAttribute MoneyTransactionFilter filter,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        HttpApiResponse<Page<MoneyTransactionResponse>> response =
                yattAdminService.getMoneyTransactions(new MoneyTransactionFilter(
                        filter.getFromUserId(), filter.getToUserId(), filter.getMoneyType(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()), PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/money-transactions/excel")
    public ResponseEntity<byte[]> getMoneyTransactionExcel(@ModelAttribute MoneyTransactionFilter filter) throws IOException {
        byte[] response =
                yattAdminService.getMoneyTransactionExcel(new MoneyTransactionFilter(
                        filter.getFromUserId(), filter.getToUserId(), filter.getMoneyType(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()));
        String filename = "money_transactions_" + OffsetDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(EXCEL_CONTENT_TYPE))
                .body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<HttpApiResponse<List<UserResponse>>> getAllUsersInYatt() {
        HttpApiResponse<List<UserResponse>> response = yattAdminService.getAllUsersInYatt();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/debits")
    public ResponseEntity<HttpApiResponse<Page<DebitResponse>>> getDebits(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            @ModelAttribute DebitFilter filter) {
        HttpApiResponse<Page<DebitResponse>> response = yattAdminService.getDebits(filter, PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/debits/excel")
    public ResponseEntity<byte[]> getDebitsExcel(@ModelAttribute DebitFilter filter) throws IOException {
        byte[] response = yattAdminService.getDebitsExcel(filter);
        String filename = "debit_" + OffsetDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(EXCEL_CONTENT_TYPE))
                .body(response);
    }

    @GetMapping("/money-transaction/{id}")
    public ResponseEntity<HttpApiResponse<MoneyTransactionResponse>> getMoneyTransactionById(
            @PathVariable Long id
    ) {
        HttpApiResponse<MoneyTransactionResponse> response = yattAdminService.getMoneyTransactionById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/product-transaction/{id}")
    public ResponseEntity<HttpApiResponse<ProductTransactionResponse>> getProductTransactionById(
            @PathVariable Long id
    ) {
        HttpApiResponse<ProductTransactionResponse> response = yattAdminService.getProductTransactionById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/kassa/{id}")
    public ResponseEntity<HttpApiResponse<KassaResponse>> getKassaById(
            @PathVariable Long id
    ) {
        HttpApiResponse<KassaResponse> response = yattAdminService.getKassaById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<HttpApiResponse<Boolean>> updateProfile(@RequestBody UserUpdateRequest request) {
        HttpApiResponse<Boolean> response = yattAdminService.updateProfile(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/password")
    public ResponseEntity<HttpApiResponse<Boolean>> updateProfilePassword(@RequestBody UserPasswordUpdate request) {
        HttpApiResponse<Boolean> response = yattAdminService.updateProfilePassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
