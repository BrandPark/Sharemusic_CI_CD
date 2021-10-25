package com.brandpark.sharemusic.modules.follow;

interface ExtendFollowRepository {
    boolean isFollowing(Long followerId, Long targetId);
}
