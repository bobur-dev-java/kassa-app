package com.company.kassa.repository;

import com.company.kassa.models.MoneyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoneyTransactionRepository extends JpaRepository<MoneyTransaction, Long>, JpaSpecificationExecutor<MoneyTransaction> {

    @Query("select mt from MoneyTransaction mt where mt.id=:id and mt.yattId=:yattId and mt.deletedAt is null")
    Optional<MoneyTransaction> findByIdAndYattId(Long id, Long yattId);
}
