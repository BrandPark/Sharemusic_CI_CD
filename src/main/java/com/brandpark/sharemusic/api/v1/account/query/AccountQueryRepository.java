package com.brandpark.sharemusic.api.v1.account.query;

import com.brandpark.sharemusic.api.v1.search.dto.UserSearchResult;
import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.account.domain.QFollow;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
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

import static com.querydsl.core.types.ExpressionUtils.count;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class AccountQueryRepository {

    private final JPAQueryFactory queryFactory;
    QAlbum album = QAlbum.album;
    QFollow follow = QFollow.follow;
    QAccount account = QAccount.account;

    public PagingDto<UserSearchResult> findAllAccountByUserName(String userName, Pageable pageable) {
        QueryResults<UserSearchResult> queryResults = queryFactory.select(Projections.fields(UserSearchResult.class,
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
}
