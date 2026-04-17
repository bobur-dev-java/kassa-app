package com.company.kassa.service.money.impl;

import com.company.kassa.config.security.UserSession;
import com.company.kassa.models.Debt;
import com.company.kassa.models.MoneyTransaction;
import com.company.kassa.repository.DebitRepository;
import com.company.kassa.repository.MoneyTransactionRepository;
import com.company.kassa.service.money.MoneyTransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MoneyTransactionServiceImpl implements MoneyTransactionService {
    private final MoneyTransactionRepository transactionRepository;
    private final DebitRepository debitRepository;
    private final UserSession userSession;

    @Override
    public Long makeMoneyTransaction(MoneyTransaction transaction) {
        MoneyTransaction saved = transactionRepository.save(transaction);

        updateDebitAmount(saved);
        return saved.getId();
    }

    private void updateDebitAmount(MoneyTransaction saved) {
        Long toUserId = saved.getToUser().getId();

        Debt debt = debitRepository.findByUserId(toUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("debit.not.found"));

        BigDecimal amount = saved.getAmount();

        BigDecimal activeAmount = debt.getActiveAmount();
        BigDecimal nonActiveAmount = debt.getNonActive();

        // Subtract from active first
        BigDecimal remaining = activeAmount.subtract(amount);

        if (remaining.compareTo(BigDecimal.ZERO) >= 0) {
            // enough in active
            debt.setActiveAmount(remaining);
        } else {
            // not enough → use nonActive
            BigDecimal deficit = remaining.abs(); // how much still needed

            BigDecimal newNonActive = nonActiveAmount.subtract(deficit);

            if (newNonActive.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("debit.can.not.be.negative");
            }

            debt.setActiveAmount(BigDecimal.ZERO);
            debt.setNonActive(newNonActive);
        }

        debitRepository.save(debt);
    }
}
