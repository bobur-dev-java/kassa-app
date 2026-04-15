package com.company.kassa.repository;

import com.company.kassa.models.YaTT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YaTTRepository extends JpaRepository<YaTT, Long> {
    @Query("select y from YaTT y where y.id=:yattId and y.deletedAt is null ")
    Optional<YaTT> findByIdAndDeletedAtIsNull(Long yattId);

    boolean existsByNameAndDeletedAtIsNull(String name);
}
