package com.brandpark.sharemusic.modules.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentEvent {
    private Long commentId;
    private Long writerId;
    private Long commentTargetAlbumId;
}
