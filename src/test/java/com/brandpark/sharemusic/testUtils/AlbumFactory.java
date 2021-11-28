package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.domain.Track;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Profile("test")
@Component
public class AlbumFactory {

    private final AlbumRepository albumRepository;
    private final EntityManager entityManager;

    public Album persistAlbumWithTracks(String title, int trackCount, Long accountId) {
        Album result = albumRepository.save(createAlbumWithTracks(title, trackCount, accountId));

        entityManager.clear();
        entityManager.flush();

        return result;
    }

    public List<Album> persistAlbumsWithTracks(String title, int albumCount, int trackCount, Long accountId) {
        List<Album> result = albumRepository.saveAll(createAlbumsWithTracks(title, albumCount, trackCount, accountId));

        entityManager.clear();
        entityManager.flush();

        return result;
    }

    public List<Album> createAlbumsWithTracks(String title, int albumCount, int trackCount, Long accountId) {
        List<Album> result = new ArrayList<>();

        for (int i = 0; i < albumCount; i++) {
            Album albumWithTracks = createAlbumWithTracks(title + i, trackCount, accountId);
            result.add(albumWithTracks);
        }

        return result;
    }

    public Album createAlbumWithTracks(String title, int trackCount, Long accountId) {
        Album album = Album.builder()
                .title(title)
                .trackCount(trackCount)
                .accountId(accountId)
                .description(title + ".description")
                .albumImage(title + ".image")
                .build();

        List<Track> tracks = createTracksContainingAlbum(trackCount, album);
        album.getTracks().addAll(tracks);

        return album;
    }

    public Album createAlbumWithTracks(int trackCount, Long accountId) {
        Album album = createAlbumWithTracks("앨범 제목", trackCount, accountId);

        return album;
    }

    public List<Track> createTracksContainingAlbum(int count, Album album) {

        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = album.getTitle() + ".track" + i + ".name";
            String artist = album.getTitle() + ".track" + i + ".artist";
            tracks.add(createTrackContainingAlbum(name, artist, album));
        }

        return tracks;
    }

    public Track createTrackContainingAlbum(String name, String artist, Album album) {
        return Track.builder()
                .name(name)
                .artist(artist)
                .album(album)
                .build();
    }
}
