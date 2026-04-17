package com.company.kassa.repository;

import com.company.kassa.models.Kassa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface KassaRepository extends JpaRepository<Kassa, Long>, JpaSpecificationExecutor<Kassa> {
}
