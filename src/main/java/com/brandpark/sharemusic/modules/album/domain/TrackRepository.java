package com.brandpark.sharemusic.modules.album.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TrackRepository extends JpaRepository<Track, Long>, ExtendTrackRepository {

    @Transactional
    @Override
    int batchInsert(List<Track> tracks);

    @Transactional
    @Override
    int batchUpdate(List<Track> tracks);

    boolean existsTrackByIdAndAlbumId(Long trackId, Long albumId);
}
