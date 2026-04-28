package com.company.kassa.controller;

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
import com.company.kassa.service.bigSeller.BigSellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/big-seller")
public class BigSellerController {
    private static final String EXCEL_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final BigSellerService bigSellerService;

    @PostMapping("/product-transaction")
    public ResponseEntity<HttpApiResponse<Long>> makeProductTransaction(@Valid @RequestBody ProductTransactionRequest request) {
        HttpApiResponse<Long> response =
                bigSellerService.makeProductTransaction(
                        null, request.getToUserId(), request.getTransactionDate(), request.getProducts());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpApiResponse<UserProfileResponse>> getProfile() {
        HttpApiResponse<UserProfileResponse> response = bigSellerService.getProfile();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/money-transaction/{id}")
    public ResponseEntity<HttpApiResponse<MoneyTransactionResponse>> getMoneyTransactionById(
            @PathVariable Long id
    ) {
        HttpApiResponse<MoneyTransactionResponse> response = bigSellerService.getMoneyTransactionById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/product-transaction/{id}")
    public ResponseEntity<HttpApiResponse<ProductTransactionResponse>> getProductTransactionById(
            @PathVariable Long id
    ) {
        HttpApiResponse<ProductTransactionResponse> response = bigSellerService.getProductTransactionById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/product-transactions")
    public ResponseEntity<HttpApiResponse<Page<ProductTransactionResponse>>> getProductTransactions(
            @ModelAttribute ProductTransactionFilter filter,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        HttpApiResponse<Page<ProductTransactionResponse>> response =
                bigSellerService.getProductTransactions(new ProductTransactionFilter(
                        null, filter.getToUserId(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()), PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/product-transactions/excel")
    public ResponseEntity<byte[]> getProductTransactionExcel(@ModelAttribute ProductTransactionFilter filter) throws IOException {
        byte[] response =
                bigSellerService.getProductTransactionExcel(new ProductTransactionFilter(
                        null, filter.getToUserId(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()));

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
                bigSellerService.getMoneyTransactions(new MoneyTransactionFilter(
                        filter.getFromUserId(), null, filter.getMoneyType(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()), PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/money-transactions/excel")
    public ResponseEntity<byte[]> getMoneyTransactionExcel(@ModelAttribute MoneyTransactionFilter filter) throws IOException {
        byte[] response =
                bigSellerService.getMoneyTransactionExcel(new MoneyTransactionFilter(
                        filter.getFromUserId(), null, filter.getMoneyType(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()));
        String filename = "money_transactions_" + OffsetDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(EXCEL_CONTENT_TYPE))
                .body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<HttpApiResponse<List<UserResponse>>> getAllUsersInYatt() {
        HttpApiResponse<List<UserResponse>> response = bigSellerService.getAllUsersInYatt();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/product-transaction/{id}")
    public ResponseEntity<HttpApiResponse<Long>> updateProductTransaction(@PathVariable Long id, @RequestBody ProductTransactionUpdate request) throws AccessDeniedException {
        HttpApiResponse<Long> response = bigSellerService.updateProductTransaction(request, id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/money-transaction/{id}")
    public ResponseEntity<HttpApiResponse<Long>> updateMoneyTransaction(@PathVariable Long id, @RequestBody MoneyTransactionUpdate request) throws AccessDeniedException {
        HttpApiResponse<Long> response = bigSellerService.updateMoneyTransaction(request, id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<HttpApiResponse<Boolean>> updateProfile(@RequestBody UserUpdateRequest request) {
        HttpApiResponse<Boolean> response = bigSellerService.updateProfile(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/password")
    public ResponseEntity<HttpApiResponse<Boolean>> updateProfilePassword(@RequestBody UserPasswordUpdate request) {
        HttpApiResponse<Boolean> response = bigSellerService.updateProfilePassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/debits")
    public ResponseEntity<HttpApiResponse<Page<DebitResponse>>> getDebits(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        HttpApiResponse<Page<DebitResponse>> response = bigSellerService.getDebits(PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/debits/excel")
    public ResponseEntity<byte[]> getDebitsExcel() throws IOException {
        byte[] response = bigSellerService.getDebitsExcel();
        String filename = "debit_" + OffsetDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(EXCEL_CONTENT_TYPE))
                .body(response);
    }

}
