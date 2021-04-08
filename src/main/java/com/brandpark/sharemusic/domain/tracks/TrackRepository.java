package com.brandpark.sharemusic.domain.tracks;

import com.brandpark.sharemusic.domain.albums.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    @Query("SELECT t FROM Track t WHERE t.album = ?1")
    List<Track> findAllWithAlbum(Album album);
}
