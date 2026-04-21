package com.company.kassa.repository;

import com.company.kassa.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.productTransaction.id=:transactionId and p.deletedAt is null")
    List<Product> findAllByTransactionId(Long transactionId);

    List<Product> findAllByProductTransactionIdIn(List<Long> transactionIds);

    @Query("select p from Product p where p.productTransaction.id=:id and p.yattId=:yattId and p.deletedAt is null")
    List<Product> getProductByProductTransaction(Long id, Long yattId);
}
