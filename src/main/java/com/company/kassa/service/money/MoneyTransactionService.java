package com.company.kassa.service.money;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.dto.money.MoneyTransactionUpdate;
import com.company.kassa.models.MoneyTransaction;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;

@Service
public interface MoneyTransactionService {
    Long makeMoneyTransaction(MoneyTransaction transaction);

    void updateMoneyTransaction(MoneyTransaction transaction, MoneyTransactionUpdate request) throws AccessDeniedException;
}
