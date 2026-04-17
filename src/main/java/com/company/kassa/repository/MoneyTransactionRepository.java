package com.company.kassa.repository;

import com.company.kassa.models.MoneyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MoneyTransactionRepository extends JpaRepository<MoneyTransaction, Long>, JpaSpecificationExecutor<MoneyTransaction> {
}
