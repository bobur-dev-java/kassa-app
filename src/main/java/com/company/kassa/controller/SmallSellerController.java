package com.company.kassa.controller;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.kassa.KassaCreateRequest;
import com.company.kassa.dto.kassa.KassaFilter;
import com.company.kassa.dto.kassa.KassaResponse;
import com.company.kassa.dto.money.MoneyTransactionFilter;
import com.company.kassa.dto.money.MoneyTransactionRequest;
import com.company.kassa.dto.money.MoneyTransactionResponse;
import com.company.kassa.dto.product.ProductTransactionFilter;
import com.company.kassa.dto.product.ProductTransactionRequest;
import com.company.kassa.dto.product.ProductTransactionResponse;
import com.company.kassa.dto.user.UserPasswordUpdate;
import com.company.kassa.dto.user.UserResponse;
import com.company.kassa.dto.user.UserUpdateRequest;
import com.company.kassa.service.small_seller.SmallSellerService;
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
@RequestMapping("/api/small-seller")
public class SmallSellerController {
    private final SmallSellerService smallSellerService;
    private static final String EXCEL_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final DateTimeFormatter FILE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");


    @PostMapping("/product-transaction")
    public ResponseEntity<HttpApiResponse<Long>> makeProductTransaction(@Valid @RequestBody ProductTransactionRequest request) {
        HttpApiResponse<Long> response =
                smallSellerService.makeProductTransaction(
                        request.getFromUserId(), null, request.getTransactionDate(), request.getProducts());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/money-transaction")
    public ResponseEntity<HttpApiResponse<Long>> makeMoneyTransaction(@Valid @RequestBody MoneyTransactionRequest request) {
        HttpApiResponse<Long> response =
                smallSellerService.makeMoneyTransaction(null, request.getToUserId(),
                        request.getAmount(), request.getTransactionDate(), request.getMoneyType());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/kassa")
    public ResponseEntity<HttpApiResponse<Long>> kassaCreate(@Valid @RequestBody KassaCreateRequest request) {
        HttpApiResponse<Long> response = smallSellerService.kassaCreate(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/kassa")
    public ResponseEntity<HttpApiResponse<Page<KassaResponse>>> getKassaFilter(
            @ModelAttribute KassaFilter kassaFilter,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        HttpApiResponse<Page<KassaResponse>> response =
                smallSellerService.getKassaFilter(new KassaFilter(null, kassaFilter.getFrom(), kassaFilter.getTo(), kassaFilter.getIsCompleted()), PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/kassa-excel")
    public ResponseEntity<byte[]> getKassaFilterExcel(@ModelAttribute KassaFilter kassaFilter) throws IOException {
        byte[] response =
                smallSellerService.getKassaFilterExcel(new KassaFilter(
                        null, kassaFilter.getFrom(), kassaFilter.getTo(), kassaFilter.getIsCompleted()));
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
                smallSellerService.getProductTransactions(new ProductTransactionFilter(
                        filter.getFromUserId(), null, filter.getFrom(), filter.getTo(), filter.getIsCompleted()), PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/product-transactions/excel")
    public ResponseEntity<byte[]> getProductTransactionExcel(@ModelAttribute ProductTransactionFilter filter) throws IOException {
        byte[] response =
                smallSellerService.getProductTransactionExcel(new ProductTransactionFilter(
                        filter.getFromUserId(), null, filter.getFrom(), filter.getTo(), filter.getIsCompleted()));

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
                smallSellerService.getMoneyTransactions(new MoneyTransactionFilter(
                        null, filter.getToUserId(), filter.getMoneyType(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()), PageRequest.of(page, size));
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/money-transactions/excel")
    public ResponseEntity<byte[]> getMoneyTransactionExcel(@ModelAttribute MoneyTransactionFilter filter) throws IOException {
        byte[] response =
                smallSellerService.getMoneyTransactionExcel(new MoneyTransactionFilter(
                        null, filter.getToUserId(), filter.getMoneyType(), filter.getFrom(), filter.getTo(), filter.getIsCompleted()));
        String filename = "money_transactions_" + OffsetDateTime.now().format(FILE_DATE_FORMATTER) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(EXCEL_CONTENT_TYPE))
                .body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<HttpApiResponse<List<UserResponse>>> getAllUsersInYatt() {
        HttpApiResponse<List<UserResponse>> response = smallSellerService.getAllUsersInYatt();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<HttpApiResponse<Boolean>> updateProfile(@RequestBody UserUpdateRequest request) {
        HttpApiResponse<Boolean> response = smallSellerService.updateProfile(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/password")
    public ResponseEntity<HttpApiResponse<Boolean>> updateProfilePassword(@RequestBody UserPasswordUpdate request) {
        HttpApiResponse<Boolean> response = smallSellerService.updateProfilePassword(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
