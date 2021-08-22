package com.brandpark.sharemusic.web.api.dto;

import com.brandpark.sharemusic.domain.Theme;
import com.brandpark.sharemusic.domain.album.Album;
import com.brandpark.sharemusic.domain.album.Track;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class AlbumSaveRequestDto {

    @NotEmpty
    private String title;

    private List<TrackSaveRequestDto> tracks = new ArrayList<>();

    private String imgUrl;

    private Set<Theme> themes;

    public Album toEntity() {

        List<Track> trackEntities = tracks.stream()
                .map(TrackSaveRequestDto::toEntity)
                .collect(Collectors.toList());

        return Album.createAlbum(title, imgUrl, trackEntities, themes);
    }
}
