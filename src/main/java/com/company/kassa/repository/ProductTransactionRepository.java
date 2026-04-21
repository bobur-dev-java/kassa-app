package com.company.kassa.repository;

import com.company.kassa.models.ProductTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long>, JpaSpecificationExecutor<ProductTransaction> {
    @Query("select pt from ProductTransaction pt where pt.id=:id and pt.yattId=:yattId and pt.deletedAt is null")
    Optional<ProductTransaction> findByIdAndYattId(Long id, Long yattId);
}
