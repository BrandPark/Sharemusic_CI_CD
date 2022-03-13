package com.brandpark.sharemusic.modules.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateAlbumEvent {
    private Long albumId;
    private Long creatorId;

    public static CreateAlbumEvent createAlbumEvent(Long albumId, Long creatorId) {
        CreateAlbumEvent ret = new CreateAlbumEvent();

        ret.albumId = albumId;
        ret.creatorId = creatorId;

        return ret;
    }
}
