package com.brandpark.sharemusic.api;

import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.album.dto.TrackSaveRequest;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AlbumFactory {

    public Album createAlbum(String title) {

        Album album = Album.builder()
                .title(title)
                .albumImage("image")
                .build();

        List<Track> tracks = createTrackList("name", "artist", 5);
        for (Track track : tracks) {
            track.initAlbum(album);
        }

        album.getTracks().addAll(tracks);
        return album;
    }

    public List<Track> createTrackList(String name, String artist, int count) {

        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tracks.add(createTrack(name + i, artist + i));
        }

        return tracks;
    }

    public Track createTrack(String name, String artist) {
        return Track.builder()
                .name(name)
                .artist(artist)
                .build();
    }

    public TrackSaveRequest createTrackSaveDto(String name, String artist) {
        TrackSaveRequest dto = new TrackSaveRequest();
        dto.setName(name);
        dto.setArtist(artist);

        return dto;
    }

    public List<TrackSaveRequest> createTrackSaveDtoList(String name, String artist, int count) {
        List<TrackSaveRequest> trackDtos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TrackSaveRequest trackDto = createTrackSaveDto(name + i, artist + i);
            trackDtos.add(trackDto);
        }

        return trackDtos;
    }

    public AlbumSaveRequest createAlbumSaveDto(String title) {
        List<TrackSaveRequest> trackDtos = createTrackSaveDtoList("name", "artist", 5);
        return createAlbumSaveDto(title, trackDtos);
    }

    public AlbumSaveRequest createAlbumSaveDto(String title, List<TrackSaveRequest> trackSaveRequests) {
        AlbumSaveRequest albumDto = new AlbumSaveRequest();
        albumDto.setTitle(title);
        albumDto.setTracks(trackSaveRequests);

        return albumDto;
    }
}
