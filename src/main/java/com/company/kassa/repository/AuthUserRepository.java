package com.company.kassa.repository;

import com.company.kassa.models.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

    @Query("select u from AuthUser u where u.username=:username and u.yattId=:yattId and u.deletedAt is null ")
    Optional<AuthUser> findByUsernameAndYaTTId(String username, Long yattId);

    @Query("select u from AuthUser u where u.id=:userId and u.yattId=:yattId and u.deletedAt is null ")
    Optional<AuthUser> findByIdAndYattIdAndDeletedAtIsNull(Long userId, Long yattId);

    @Query("""
                select count(u.id) > 0 from AuthUser u where u.username <> :username and u.yattId = :yattId
            """)
    boolean existsByUsername(String username, Long yattId);

    @Query("select u from AuthUser u where u.yattId=:yattId and u.deletedAt is null ")
    List<AuthUser> findAllByYattId(Long yattId);
}
