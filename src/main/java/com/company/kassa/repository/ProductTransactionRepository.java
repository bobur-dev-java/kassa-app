package com.company.kassa.repository;

import com.company.kassa.models.ProductTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTransactionRepository extends JpaRepository<ProductTransaction, Long>, JpaSpecificationExecutor<ProductTransaction> {
}
