package com.company.kassa.repository;

import com.company.kassa.models.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface DebitRepository extends JpaRepository<Debt, Long> {

    @Query("select d from Debt d where d.fromUser.id=:id and d.yattId=:yattId and d.deletedAt is null ")
    Optional<Debt> findByUserId(Long id, Long yattId);

    @Modifying
    @Query("""
            UPDATE Debt d
            SET d.activeAmount = d.activeAmount + :amount
            WHERE d.fromUser.id = :userId AND d.yattId = :yattId
            """)
    int incrementActiveAmount(@Param("userId") Long userId,
                              @Param("yattId") Long yattId,
                              @Param("amount") BigDecimal amount);
}
