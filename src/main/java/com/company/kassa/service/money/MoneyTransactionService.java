package com.company.kassa.service.money;

import com.company.kassa.dto.HttpApiResponse;
import com.company.kassa.models.MoneyTransaction;
import org.springframework.stereotype.Service;

@Service
public interface MoneyTransactionService {
    Long makeMoneyTransaction(MoneyTransaction transaction);
}
