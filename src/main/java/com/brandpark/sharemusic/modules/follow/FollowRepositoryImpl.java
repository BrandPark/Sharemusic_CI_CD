package com.brandpark.sharemusic.modules.follow;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FollowRepositoryImpl implements ExtendFollowRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean isFollowing(Long followerId, Long targetId) {
        QFollow follow = QFollow.follow;

        Follow queryResult = queryFactory.selectFrom(follow)
                .where(follow.follower.id.eq(followerId).and(follow.target.id.eq(targetId)))
                .fetchOne();

        return queryResult != null;
    }
}
