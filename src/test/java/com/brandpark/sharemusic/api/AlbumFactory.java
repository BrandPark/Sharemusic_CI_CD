package com.brandpark.sharemusic.api;

import com.brandpark.sharemusic.modules.album.dto.AlbumSaveDto;
import com.brandpark.sharemusic.modules.album.dto.TrackSaveDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AlbumFactory {

    public TrackSaveDto createTrackSaveDto(String name, String artist) {
        TrackSaveDto dto = new TrackSaveDto();
        dto.setName(name);
        dto.setArtist(artist);

        return dto;
    }

    public List<TrackSaveDto> createTrackSaveDtoList(String name, String artist, int count) {
        List<TrackSaveDto> trackDtos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TrackSaveDto trackDto = createTrackSaveDto(name + i, artist + i);
            trackDtos.add(trackDto);
        }

        return trackDtos;
    }

    public AlbumSaveDto createAlbumSaveDto(String title, List<TrackSaveDto> trackSaveDtos) {
        AlbumSaveDto albumDto = new AlbumSaveDto();
        albumDto.setTitle(title);
        albumDto.setTracks(trackSaveDtos);

        return albumDto;
    }
}
