package com.brandpark.sharemusic.modules.album.dto;

import com.brandpark.sharemusic.modules.album.domain.TrackStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class UpdateAlbumDto {
    private String title;

    private String description;

    private String albumImage;

    private List<UpdateTrackDto> tracks;

    @Data
    public static class UpdateTrackDto {
        private Long id;

        private String name;

        private String artist;

        private TrackStatus status;
    }
}
