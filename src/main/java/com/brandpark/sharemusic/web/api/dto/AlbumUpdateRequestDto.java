package com.brandpark.sharemusic.web.api.dto;

import com.brandpark.sharemusic.domain.Theme;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
public class AlbumUpdateRequestDto {
    private String title;
    private String imgUrl;
    private List<TrackDto> tracks;
    private Set<Theme> themes;

    @Getter
    @Setter
    public static class TrackDto {
        private Long id;
        private String name;
        private String artist;
    }
}
