package com.brandpark.sharemusic.modules.album.form;

import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.Track;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class AlbumDetailInfoForm {
    private Long id;
    private String title;
    private String albumImage;
    private String description;
    private List<TrackDetailForm> tracks = new ArrayList<>();
    private String creator;
    private String creatorProfileImage;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public AlbumDetailInfoForm(Album album, Account account) {
        id = album.getId();
        title = album.getTitle();
        albumImage = album.getAlbumImage();
        description = album.getDescription();
        creator = account.getNickname();
        creatorProfileImage = account.getProfileImage();
        createdDate = album.getCreatedDate();
        modifiedDate = album.getModifiedDate();
        tracks = album.getTracks().stream()
                .map(TrackDetailForm::new)
                .collect(Collectors.toList());
    }

    @Data
    private static class TrackDetailForm {
        private Long id;
        private String name;
        private String artist;

        public TrackDetailForm(Track track) {
            id = track.getId();
            name = track.getName();
            artist = track.getArtist();
        }
    }
}
