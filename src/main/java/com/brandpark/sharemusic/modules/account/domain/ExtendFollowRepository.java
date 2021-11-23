package com.brandpark.sharemusic.modules.account.domain;

public interface ExtendFollowRepository {
    boolean isFollowing(Long followerId, Long targetId);
}
