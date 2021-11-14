package com.brandpark.sharemusic.modules.follow.domain;

import java.util.List;
import java.util.Map;

interface ExtendFollowRepository {
    boolean isFollowing(Long followerId, Long targetId);
    Map<Long, Boolean> getFollowingStateByOtherAccountFollowerIds(List<Long> followerIds, Long loginAccountId);
    Map<Long, Boolean> getFollowingStateByFollowingIds(List<Long> followingIds, Long loginAccountId);
}
