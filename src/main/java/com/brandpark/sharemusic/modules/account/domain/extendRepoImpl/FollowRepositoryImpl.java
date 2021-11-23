package com.brandpark.sharemusic.modules.account.domain.extendRepoImpl;

import com.brandpark.sharemusic.modules.account.domain.ExtendFollowRepository;
import com.brandpark.sharemusic.modules.account.domain.Follow;
import com.brandpark.sharemusic.modules.account.domain.QFollow;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FollowRepositoryImpl implements ExtendFollowRepository {

    private final JPAQueryFactory queryFactory;
    QFollow follow = QFollow.follow;

    @Override
    public boolean isFollowing(Long followerId, Long targetId) {
        Follow queryResult = queryFactory.selectFrom(follow)
                .where(follow.follower.id.eq(followerId).and(follow.target.id.eq(targetId)))
                .fetchOne();

        return queryResult != null;
    }
}
