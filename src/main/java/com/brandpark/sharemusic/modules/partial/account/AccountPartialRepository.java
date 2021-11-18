package com.brandpark.sharemusic.modules.partial.account;

import com.brandpark.sharemusic.modules.follow.domain.QFollow;
import com.brandpark.sharemusic.modules.partial.account.form.FollowerInfoForm;
import com.brandpark.sharemusic.modules.partial.account.form.FollowingInfoForm;
import com.brandpark.sharemusic.modules.util.page.PagingDtoFactory;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class AccountPartialRepository {

    private final JPAQueryFactory queryFactory;
    private QFollow follow = QFollow.follow;
    private QFollow followSub = new QFollow("followSub");

    public PagingDto<FollowerInfoForm> findAllFollowersByAccountId(Pageable pageable, Long targetAccountId, Long myAccountId) {

        QueryResults<FollowerInfoForm> queryResults = queryFactory.select(Projections.fields(FollowerInfoForm.class,
                        follow.follower.id.as("followerId"),
                        follow.follower.profileImage,
                        follow.follower.nickname,
                        follow.follower.name,
                        follow.createdDate.as("followDate"),
                        ExpressionUtils.as(
                                JPAExpressions.select(followSub.follower.id)
                                        .from(followSub)
                                        .where(followSub.target.id.eq(follow.follower.id))
                                        .contains(myAccountId)
                                , "followingState")
                ))
                .from(follow)
                .where(follow.target.id.eq(targetAccountId))
                .orderBy(follow.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 5);
    }

    public PagingDto<FollowingInfoForm> findAllFollowingsByAccountId(Pageable pageable, Long targetAccountId, Long myAccountId) {

        QueryResults<FollowingInfoForm> queryResults = queryFactory.select(Projections.fields(FollowingInfoForm.class,
                        follow.target.id.as("followerId"),
                        follow.target.profileImage,
                        follow.target.nickname,
                        follow.target.name,
                        follow.createdDate.as("followDate"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(followSub.follower.id)
                                        .from(followSub)
                                        .where(followSub.target.id.eq(follow.target.id))
                                        .contains(myAccountId)
                                , "followingState")
                ))
                .from(follow)
                .where(follow.follower.id.eq(targetAccountId))
                .orderBy(follow.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 5);
    }

}
