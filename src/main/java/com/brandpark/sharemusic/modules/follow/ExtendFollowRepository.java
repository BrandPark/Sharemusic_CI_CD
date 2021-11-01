package com.brandpark.sharemusic.modules.follow;

import java.util.List;
import java.util.Map;

interface ExtendFollowRepository {
    boolean isFollowing(Long followerId, Long targetId);
    Map<Long, Boolean> getFollowingStateByFollowerIds(List<Long> followerIds, Long loginAccountId);
    Map<Long, Boolean> getFollowingStateByFollowingIds(List<Long> followingIds, Long loginAccountId);
}
