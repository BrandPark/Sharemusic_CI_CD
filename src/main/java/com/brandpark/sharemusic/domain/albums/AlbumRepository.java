package com.brandpark.sharemusic.domain.albums;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT a FROM Album a ORDER BY a.modifiedDate DESC")
    List<Album> findAllDesc();
}
