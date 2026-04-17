package com.company.kassa.service.money.impl;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.models.MoneyTransaction;
import com.company.kassa.service.money.MoneyTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoneyTransactionServiceImpl implements MoneyTransactionService {
    @Override
    public HttpApiResponse<Long> makeMoneyTransaction(MoneyTransaction transaction) {
        return null;
    }
}
