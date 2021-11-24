package com.brandpark.sharemusic.testUtils;

import com.brandpark.sharemusic.api.v1.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackUpdateRequest;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.domain.Track;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Profile("test")
@Component
public class AlbumFactory {

    private final ModelMapper modelMapper;
    private final AlbumRepository albumRepository;

    public Album persistAlbumWithTracks(String title, int trackCount, Long accountId) {
        return albumRepository.save(createAlbumWithTracks(title, trackCount, accountId));
    }

    public List<Album> persistAlbumsWithTracks(String title, int albumCount, int trackCount, Long accountId) {
        return albumRepository.saveAll(createAlbumsWithTracks(title, albumCount, trackCount, accountId));
    }

    public List<Album> createAlbumsWithTracks(String title, int albumCount, int trackCount, Long accountId) {
        List<Album> result = new ArrayList<>();

        for (int i = 0; i < albumCount; i++) {
            Album albumWithTracks = createAlbumWithTracks("title" + i, trackCount, accountId);
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

    public List<TrackSaveRequest> createTrackSaveDtos(int trackCount) {
        List<TrackSaveRequest> tracks = new ArrayList<>();
        for (int i = 0; i < trackCount; i++) {
            TrackSaveRequest trackDto = createTrackSaveDto("이름" + i, "아티스트" + i);
            tracks.add(trackDto);
        }
        return tracks;
    }

    public TrackSaveRequest createTrackSaveDto(String name, String artist) {
        TrackSaveRequest trackDto = new TrackSaveRequest();
        trackDto.setName(name);
        trackDto.setArtist(artist);

        return trackDto;
    }

    public AlbumSaveRequest createAlbumSaveDto() {
        AlbumSaveRequest albumDto = new AlbumSaveRequest();
        albumDto.setTitle("앨범 제목");
        albumDto.setDescription("앨범 소개");
        albumDto.setAlbumImage("앨범 이미지");
        return albumDto;
    }

    public AlbumUpdateRequest createAlbumUpdateDtoByEntity(Album album) {
        return modelMapper.map(album, AlbumUpdateRequest.class);
    }

    public TrackUpdateRequest createTrackUpdateDto(String name, String artist) {
        TrackUpdateRequest trackDto = new TrackUpdateRequest();
        trackDto.setName(name);
        trackDto.setArtist(artist);

        return trackDto;
    }

    public Comment createComment(Long albumId, Long accountId, String content) {
        return Comment.builder()
                .albumId(albumId)
                .accountId(accountId)
                .content(content)
                .build();
    }

}
