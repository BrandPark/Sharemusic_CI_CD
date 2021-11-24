package com.brandpark.sharemusic.api.v1.follow.query;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.follow.dto.FollowerInfoResponse;
import com.brandpark.sharemusic.api.v1.follow.dto.FollowingInfoResponse;
import com.brandpark.sharemusic.modules.account.domain.QFollow;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class FollowQueryRepository {

    private final JPAQueryFactory queryFactory;
    private QFollow follow = QFollow.follow;

    public PageResult<FollowerInfoResponse> findAllFollowersByPaging(Long targetAccountId, Pageable pageable) {

        QueryResults<FollowerInfoResponse> queryResults = queryFactory.select(Projections.fields(FollowerInfoResponse.class,
                        follow.follower.id.as("followerId"),
                        follow.follower.profileImage,
                        follow.follower.nickname,
                        follow.follower.name,
                        follow.createdDate.as("followDate")
                ))
                .from(follow)
                .where(follow.target.id.eq(targetAccountId))
                .orderBy(follow.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PageResultFactory.createPageResult(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    public PageResult<FollowingInfoResponse> findAllFollowingsByPaging(Long accountId, Pageable pageable) {

        QueryResults<FollowingInfoResponse> queryResults = queryFactory.select(Projections.fields(FollowingInfoResponse.class,
                        follow.target.id.as("followingId"),
                        follow.target.profileImage,
                        follow.target.nickname,
                        follow.target.name,
                        follow.createdDate.as("followingDate")
                ))
                .from(follow)
                .where(follow.follower.id.eq(accountId))
                .orderBy(follow.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PageResultFactory.createPageResult(queryResults.getResults(), pageable, queryResults.getTotal());
    }
}
