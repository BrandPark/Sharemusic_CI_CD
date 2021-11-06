package com.brandpark.sharemusic.modules.follow.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, ExtendFollowRepository {

    Optional<Follow> findByFollowerIdAndTargetId(Long followerId, Long targetId);

    @Override
    Map<Long, Boolean> getFollowingStateByOtherAccountFollowerIds(List<Long> followerIds, Long loginAccountId);

    @Override
    boolean isFollowing(Long followerId, Long targetId);
}
