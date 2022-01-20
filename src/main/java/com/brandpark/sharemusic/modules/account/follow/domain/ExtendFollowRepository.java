package com.brandpark.sharemusic.modules.account.follow.domain;

public interface ExtendFollowRepository {
    boolean isFollowing(Long followerId, Long targetId);
}
