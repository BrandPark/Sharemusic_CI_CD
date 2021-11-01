package com.brandpark.sharemusic.modules.follow;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<Long, Boolean> getFollowingStateByFollowerIds(List<Long> followerIds, Long loginAccountId) {
        List<Follow> follows = queryFactory.selectFrom(follow)
                .where(follow.follower.id.eq(loginAccountId).not().and(follow.follower.id.in(followerIds)))
                .fetch();

        Map<Long, Boolean> result = new HashMap<>();

        for (Follow f : follows) {
            boolean isFollowing = false;
            if (f.getFollower().getId().equals(loginAccountId)) {
                isFollowing = true;
            }
            result.put(f.getFollower().getId(), isFollowing);
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
