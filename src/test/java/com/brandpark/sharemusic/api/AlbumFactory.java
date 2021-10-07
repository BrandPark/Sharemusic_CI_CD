package com.brandpark.sharemusic.api;

import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.album.dto.TrackSaveRequest;
import com.brandpark.sharemusic.api.album.dto.TrackUpdateRequest;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Profile("test")
@Component
public class AlbumFactory {

    @Autowired ModelMapper modelMapper;

    public Album createAlbum(String title) {

        Album album = Album.builder()
                .title(title)
                .accountId(1L)
                .bio("My name is " + title)
                .albumImage("image")
                .build();

        List<Track> tracks = createTrackList(title+".name", title+".artist", 5, album);

        album.getTracks().addAll(tracks);
        return album;
    }

    public List<Track> createTrackList(String name, String artist, int count, Album album) {

        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tracks.add(createTrack(name + i, artist + i, album));
        }

        return tracks;
    }

    public Track createTrack(String name, String artist, Album album) {
        return Track.builder()
                .name(name)
                .artist(artist)
                .album(album)
                .build();
    }

    public AlbumSaveRequest createAlbumSaveDto(String title) {
        List<TrackSaveRequest> trackDtos = createTrackSaveDtoList("name", "artist", 5);
        return createAlbumSaveDto(title, trackDtos);
    }

    public AlbumSaveRequest createAlbumSaveDto(String title, List<TrackSaveRequest> trackSaveRequests) {
        AlbumSaveRequest albumDto = new AlbumSaveRequest();
        albumDto.setTitle(title);
        albumDto.setBio("My name is " + title);
        albumDto.setTracks(trackSaveRequests);

        return albumDto;
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

    public AlbumUpdateRequest createAlbumUpdateDto(Album album) {
        return modelMapper.map(album, AlbumUpdateRequest.class);
    }

    public TrackUpdateRequest createTrackUpdateDto() {
        Track track = createTrack("음원명", "아티스트", createAlbum("title"));
        return createTrackUpdateDto(track);
    }

    public TrackUpdateRequest createTrackUpdateDto(Track track) {
        return modelMapper.map(track, TrackUpdateRequest.class);
    }
}
