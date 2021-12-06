package com.brandpark.sharemusic.modules.follow.domain;

public interface ExtendFollowRepository {
    boolean isFollowing(Long followerId, Long targetId);
}
