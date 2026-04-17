package com.company.kassa.repository;

import com.company.kassa.models.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DebitRepository extends JpaRepository<Debt, Long> {

    @Query("select d from Debt d where d.fromUser.id=:id and d.yattId=:yattId and d.deletedAt is null ")
    Optional<Debt> findByUserId(Long id, Long yattId);
}
