package com.brandpark.sharemusic.modules.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String nickname);

    @Query("SELECT a FROM Account a WHERE a.email = :emailOrNickname OR a.nickname = :emailOrNickname")
    Optional<Account> findByEmailOrNickname(@Param("emailOrNickname") String emailOrNickname);
}