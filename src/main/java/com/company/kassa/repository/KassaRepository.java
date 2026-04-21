package com.company.kassa.repository;

import com.company.kassa.models.Kassa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KassaRepository extends JpaRepository<Kassa, Long>, JpaSpecificationExecutor<Kassa> {

    @Query("select k from Kassa k where k.id=:id and k.deletedAt is null and k.yattId=:yattId")
    Optional<Kassa> findByIdAndYattId(Long id, Long yattId);
}
