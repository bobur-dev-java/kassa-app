package com.company.kassa.repository;

import com.company.kassa.models.YaTTUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface YaTTUsersRepository extends JpaRepository<YaTTUsers, Long> {

    @Query("select yu from YaTTUsers yu where yu.user.username=:username and yu.yaTT.id=:yattId and yu.deletedAt is null ")
    Optional<YaTTUsers> findYattUserRole(String username, Long yattId);
}
