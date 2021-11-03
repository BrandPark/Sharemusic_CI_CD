package com.brandpark.sharemusic.api.v1.account.query;

import com.brandpark.sharemusic.api.PagingDtoFactory;
import com.brandpark.sharemusic.api.v1.account.dto.FollowerInfoDto;
import com.brandpark.sharemusic.api.v1.account.dto.FollowingInfoDto;
import com.brandpark.sharemusic.api.v1.account.query.dto.ActivityDataResponse;
import com.brandpark.sharemusic.api.v2.dto.PagingDto;
import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.brandpark.sharemusic.modules.follow.QFollow;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.querydsl.core.types.ExpressionUtils.count;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class AccountQueryRepository {

    private final JPAQueryFactory queryFactory;
    QAlbum album = QAlbum.album;
    QFollow follow = QFollow.follow;
    QAccount account = QAccount.account;

    public ActivityDataResponse findActivityData(Long accountId) {
        return queryFactory.select(Projections.fields(ActivityDataResponse.class,
                        ExpressionUtils.as(
                                JPAExpressions.select(count(album.id))
                                        .from(album)
                                        .where(album.accountId.eq(account.id)),
                                "albumCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(count(follow.id))
                                        .from(follow)
                                        .where(follow.target.eq(account)),
                                "followerCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(count(follow.id))
                                        .from(follow)
                                        .where(follow.follower.eq(account)),
                                "followingCount"
                        )
                ))
                .from(account)
                .where(account.id.eq(accountId))
                .fetchOne();
    }

    public PagingDto<FollowerInfoDto> findAllFollowersByPaging(Long accountId, Pageable pageable) {

        QueryResults<FollowerInfoDto> queryResults = queryFactory.select(Projections.fields(FollowerInfoDto.class,
                        follow.follower.id.as("followerId"),
                        follow.follower.profileImage,
                        follow.follower.nickname,
                        follow.follower.name,
                        follow.createDate.as("followDate")
                ))
                .from(follow)
                .where(follow.target.id.eq(accountId))
                .orderBy(follow.createDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 5);
    }

    public PagingDto<FollowingInfoDto> findAllFollowingsByPaging(Long accountId, Pageable pageable) {

        QueryResults<FollowingInfoDto> queryResults = queryFactory.select(Projections.fields(FollowingInfoDto.class,
                        follow.target.id.as("followingId"),
                        follow.target.profileImage,
                        follow.target.nickname,
                        follow.target.name,
                        follow.createDate.as("followingDate")
                ))
                .from(follow)
                .where(follow.follower.id.eq(accountId))
                .orderBy(follow.createDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 5);
    }
}