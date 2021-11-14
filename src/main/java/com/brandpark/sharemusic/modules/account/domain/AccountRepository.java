package com.brandpark.sharemusic.modules.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Account findByEmail(String email);

    Account findByNickname(String nickname);

    @Query("SELECT a FROM Account a WHERE a.email = :emailOrNickname OR a.nickname = :emailOrNickname")
    Optional<Account> findByEmailOrNickname(@Param("emailOrNickname") String emailOrNickname);

    @Query("SELECT a FROM Account a JOIN Album ab ON a.id = ab.accountId WHERE ab.id = :albumId")
    Optional<Account> findByAlbumId(@Param("albumId") Long albumId);

    @Query("SELECT a FROM Account a JOIN Follow f ON a.id = f.follower.id WHERE f.target.id = :followingTargetId")
    List<Account> findAllFollowersByFollowingTargetId(@Param("followingTargetId") Long followingTargetId);
}
