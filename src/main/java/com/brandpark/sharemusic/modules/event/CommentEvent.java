package com.brandpark.sharemusic.modules.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentEvent {
    private Long commentId;
    private Long writerId;
    private Long commentTargetAlbumId;

    public static CommentEvent createCommentEvent(Long commentId, Long writerId, Long commentTargetAlbumId) {
        CommentEvent ret = new CommentEvent();

        ret.commentId = commentId;
        ret.writerId = writerId;
        ret.commentTargetAlbumId = commentTargetAlbumId;

        return ret;
    }
}
