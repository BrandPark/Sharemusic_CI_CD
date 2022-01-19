package com.brandpark.sharemusic.modules.event;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowEvent {
    private Long followerId;
    private Long followingTargetId;
}
