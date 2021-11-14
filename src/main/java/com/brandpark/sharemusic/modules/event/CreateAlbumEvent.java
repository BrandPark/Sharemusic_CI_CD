package com.brandpark.sharemusic.modules.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateAlbumEvent {
    private Long albumId;
    private Long creatorId;
}
