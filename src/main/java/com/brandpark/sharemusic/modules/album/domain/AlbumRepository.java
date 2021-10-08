package com.brandpark.sharemusic.modules.album.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface AlbumRepository extends JpaRepository<Album, Long> {
    boolean existsByAccountIdAndTitle(Long accountId, String title);

    Optional<Album> findByAccountIdAndTitle(Long accountId, String title);
}
