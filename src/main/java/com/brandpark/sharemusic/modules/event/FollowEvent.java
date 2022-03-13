package com.brandpark.sharemusic.modules.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FollowEvent {
    private Long followerId;
    private Long followingTargetId;

    public static FollowEvent createFollowEvent(Long followerId, Long followingTargetId) {
        FollowEvent ret = new FollowEvent();
        ret.followerId = followerId;
        ret.followingTargetId = followingTargetId;

        return ret;
    }
}
