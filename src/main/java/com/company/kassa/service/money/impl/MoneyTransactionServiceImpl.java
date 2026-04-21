package com.company.kassa.service.money.impl;

import com.company.kassa.config.security.UserSession;
import com.company.kassa.dto.money.MoneyTransactionUpdate;
import com.company.kassa.models.Debt;
import com.company.kassa.models.MoneyTransaction;
import com.company.kassa.models.enums.YaTTUserRole;
import com.company.kassa.repository.DebitRepository;
import com.company.kassa.repository.MoneyTransactionRepository;
import com.company.kassa.service.money.MoneyTransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class MoneyTransactionServiceImpl implements MoneyTransactionService {

    private final MoneyTransactionRepository transactionRepository;
    private final DebitRepository debitRepository;
    private final UserSession userSession;

    @Override
    @Transactional
    public Long makeMoneyTransaction(MoneyTransaction transaction) {
        MoneyTransaction saved = transactionRepository.save(transaction);
        // MoneyTransaction — bu to'lov, debt kamaytirish
        subtractFromDebt(saved.getToUser().getId(), saved.getAmount());
        return saved.getId();
    }

    @Override
    @Transactional
    public void updateMoneyTransaction(MoneyTransaction transaction,
                                       MoneyTransactionUpdate request) throws AccessDeniedException {
        Long currentUserId = userSession.userId();
        YaTTUserRole currentRole = userSession.getCurrentUserRole();

        // 1. isCompleted — faqat toUser o'zgartira oladi
        if (request.getIsCompleted() != null) {
            if (!transaction.getToUser().getId().equals(currentUserId)) {
                throw new AccessDeniedException("you.cant.update.isCompleted");
            }
            transaction.setIsCompleted(request.getIsCompleted());
        }

        // 2. Amount — faqat fromUser yoki ADMIN o'zgartira oladi
        if (request.getAmount() != null) {
            boolean isFromUser = transaction.getFromUser().getId().equals(currentUserId);
            boolean isAdmin = currentRole == YaTTUserRole.YATT_ADMIN;

            if (!isFromUser && !isAdmin) {
                throw new AccessDeniedException("you.can.not.update");
            }

            BigDecimal oldAmount = transaction.getAmount();
            BigDecimal newAmount = request.getAmount();
            BigDecimal diff = newAmount.subtract(oldAmount); // + yoki -

            transaction.setAmount(newAmount);

            // Debt qayta hisoblash — farq bo'yicha
            if (diff.compareTo(BigDecimal.ZERO) != 0) {
                adjustDebt(transaction.getToUser().getId(), diff);
            }
        }

        transactionRepository.save(transaction);
    }


    /**
     * MoneyTransaction kelganda — debt dan ayirish
     * active dan avval, yetmasa nonActive dan
     */
    private void subtractFromDebt(Long toUserId, BigDecimal amount) {
        Debt debt = debitRepository.findByUserId(toUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("debit.not.found"));

        applySubtraction(debt, amount);
        debitRepository.save(debt);
    }

    /**
     * Amount update bo'lganda — diff bo'yicha adjust
     * diff > 0 : yangi amount katta — ko'proq to'langan, debt yana kamayadi
     * diff < 0 : yangi amount kichik — kam to'langan, debt qaytib oshadi
     */
    private void adjustDebt(Long toUserId, BigDecimal diff) {
        Debt debt = debitRepository.findByUserId(toUserId, userSession.yattId())
                .orElseThrow(() -> new EntityNotFoundException("debit.not.found"));

        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            // Ko'proq to'landi — yana ayirish
            applySubtraction(debt, diff);
        } else {
            // Kam to'landi — qaytarib qo'shish (activeAmount ga)
            debt.setActiveAmount(debt.getActiveAmount().add(diff.abs()));
        }

        debitRepository.save(debt);
    }

    /**
     * Active dan avval ayir, yetmasa nonActive dan
     */
    private void applySubtraction(Debt debt, BigDecimal amount) {
        BigDecimal remaining = debt.getActiveAmount().subtract(amount);

        if (remaining.compareTo(BigDecimal.ZERO) >= 0) {
            debt.setActiveAmount(remaining);
        } else {
            BigDecimal deficit = remaining.abs();
            BigDecimal newNonActive = debt.getNonActive().subtract(deficit);

            if (newNonActive.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("debit.can.not.be.negative");
            }

            debt.setActiveAmount(BigDecimal.ZERO);
            debt.setNonActive(newNonActive);
        }
    }
}
