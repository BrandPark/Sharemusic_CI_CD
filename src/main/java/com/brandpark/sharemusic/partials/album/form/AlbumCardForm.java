package com.brandpark.sharemusic.partials.album.form;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AlbumCardForm {
    private Long albumId;
    private String title;
    private String description;
    private String albumImage;
    private int trackCount;
    private String creatorNickname;
    private String creatorProfileImage;
    private List<TrackInfoForm> tracks;
    private LocalDateTime createdDate;
}
