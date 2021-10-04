package com.brandpark.sharemusic.modules.album.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AlbumRepository extends JpaRepository<Album, Long> {

}
