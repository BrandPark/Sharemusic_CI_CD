package com.brandpark.sharemusic.partials.account;

import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.brandpark.sharemusic.modules.follow.domain.QFollow;
import com.brandpark.sharemusic.modules.util.page.PagingDtoFactory;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.partials.account.form.FollowerInfoForm;
import com.brandpark.sharemusic.partials.account.form.FollowingInfoForm;
import com.brandpark.sharemusic.partials.account.form.SuggestAccountForm;
import com.brandpark.sharemusic.partials.account.form.UserCardForm;
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

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class AccountPartialRepository {

    private final JPAQueryFactory queryFactory;
    private QFollow follow = QFollow.follow;
    private QFollow followSub = new QFollow("followSub");
    private QAccount account = QAccount.account;
    private QAlbum album = QAlbum.album;

    public PagingDto<FollowerInfoForm> findAllFollowersByAccountId(Pageable pageable, Long targetAccountId) {
        QueryResults<FollowerInfoForm> queryResults = queryFactory.select(Projections.fields(FollowerInfoForm.class,
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

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 5);
    }

    public PagingDto<FollowerInfoForm> findAllFollowersWithStateByAccountId(Pageable pageable, Long targetAccountId, Long myAccountId) {

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

    public PagingDto<FollowingInfoForm> findAllFollowingsWithStateByAccountId(Pageable pageable, Long targetAccountId, Long myAccountId) {

        QueryResults<FollowingInfoForm> queryResults = queryFactory.select(Projections.fields(FollowingInfoForm.class,
                        follow.target.id.as("followingId"),
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

    public PagingDto<FollowingInfoForm> findAllFollowingsByAccountId(Pageable pageable, Long targetAccountId) {
        QueryResults<FollowingInfoForm> queryResults = queryFactory.select(Projections.fields(FollowingInfoForm.class,
                        follow.target.id.as("followingId"),
                        follow.target.profileImage,
                        follow.target.nickname,
                        follow.target.name,
                        follow.createdDate.as("followDate")
                ))
                .from(follow)
                .where(follow.follower.id.eq(targetAccountId))
                .orderBy(follow.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 5);
    }

    public PagingDto<UserCardForm> findAllAccountByUserName(Pageable pageable, String userName) {
        QueryResults<UserCardForm> queryResults = queryFactory.select(Projections.fields(UserCardForm.class,
                        account.id.as("accountId"),
                        account.name,
                        account.nickname,
                        account.bio,
                        account.profileImage,
                        account.createdDate,
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
                .where(
                        account.name.containsIgnoreCase(userName)
                                .or(account.nickname.containsIgnoreCase(userName))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 10);
    }

    public PagingDto<SuggestAccountForm> findAllAccountExceptMe(Pageable pageable, Long accountId) {
        QueryResults<SuggestAccountForm> queryResults = queryFactory.select(Projections.fields(
                        SuggestAccountForm.class,
                        account.id.as("accountId"),
                        account.name,
                        account.nickname,
                        account.profileImage
                )).from(account)
                .where(account.id.eq(accountId).not())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal());
    }
}
