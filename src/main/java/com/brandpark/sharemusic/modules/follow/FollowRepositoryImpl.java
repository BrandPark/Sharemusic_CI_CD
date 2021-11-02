package com.brandpark.sharemusic.modules.follow;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class FollowRepositoryImpl implements ExtendFollowRepository{

    private final JPAQueryFactory queryFactory;
    QFollow follow = QFollow.follow;

    @Override
    public boolean isFollowing(Long followerId, Long targetId) {
        Follow queryResult = queryFactory.selectFrom(follow)
                .where(follow.follower.id.eq(followerId).and(follow.target.id.eq(targetId)))
                .fetchOne();

        return queryResult != null;
    }

    @Override
    public Map<Long, Boolean> getFollowingStateByOtherAccountFollowerIds(List<Long> otherAccountFollowerIds, Long loginAccountId) {

        List<Long> myFollowingTargetIdList = queryFactory.selectFrom(follow)
                .where(follow.follower.id.eq(loginAccountId))
                .fetch().stream()
                .map(follow -> follow.getTarget().getId())
                .collect(Collectors.toList());

        Map<Long, Boolean> result = new HashMap<>();

        for (Long otherAccountFollowerId : otherAccountFollowerIds) {
            boolean followingState = false;

            if (myFollowingTargetIdList.contains(otherAccountFollowerId)) {
                followingState = true;
            }

            result.put(otherAccountFollowerId, followingState);
        }

        return result;
    }

    @Override
    public Map<Long, Boolean> getFollowingStateByFollowingIds(List<Long> followingIds, Long loginAccountId) {
        List<Follow> follows = queryFactory.selectFrom(follow)
                .where(follow.target.id.eq(loginAccountId).not().and(follow.target.id.in(followingIds)))
                .fetch();

        Map<Long, Boolean> result = new HashMap<>();

        for (Follow f : follows) {
            boolean isFollowing = false;
            if (f.getFollower().getId().equals(loginAccountId)) {
                isFollowing = true;
            }
            result.put(f.getTarget().getId(), isFollowing);
        }

        return result;
    }

}
