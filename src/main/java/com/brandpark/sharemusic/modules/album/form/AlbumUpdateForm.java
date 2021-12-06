package com.brandpark.sharemusic.modules.album.form;

import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import com.brandpark.sharemusic.modules.album.domain.TrackStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class AlbumUpdateForm {

    private Long id;

    private String title;

    private String description;

    private String albumImage;

    private List<TrackUpdateForm> tracks = new ArrayList<>();

    public AlbumUpdateForm(Album album) {
        id = album.getId();
        title = album.getTitle();
        description = album.getDescription();
        albumImage = album.getAlbumImage();
        tracks = album.getTracks().stream()
                .map(TrackUpdateForm::new)
                .collect(Collectors.toList());
    }

    @Data
    private static class TrackUpdateForm {

        private Long id;
        private String name;
        private String artist;
        private TrackStatus status;

        public TrackUpdateForm(Track track) {
            id = track.getId();
            name = track.getName();
            artist = track.getArtist();
            status = TrackStatus.NONE;
        }
    }
}
