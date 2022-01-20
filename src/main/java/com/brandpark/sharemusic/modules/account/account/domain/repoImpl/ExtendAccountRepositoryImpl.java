package com.brandpark.sharemusic.modules.account.account.domain.repoImpl;

import com.brandpark.sharemusic.modules.account.account.domain.ExtendAccountRepository;
import com.brandpark.sharemusic.modules.account.account.domain.QAccount;
import com.brandpark.sharemusic.modules.account.account.form.FriendshipDataForm;
import com.brandpark.sharemusic.modules.account.follow.domain.QFollow;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.querydsl.core.types.ExpressionUtils.count;

@RequiredArgsConstructor
public class ExtendAccountRepositoryImpl implements ExtendAccountRepository {

    private final JPAQueryFactory queryFactory;

    private QAlbum album = QAlbum.album;
    private QAccount account = QAccount.account;
    private QFollow follow = QFollow.follow;

    public FriendshipDataForm findFriendshipData(Long accountId) {
        return queryFactory.select(Projections.fields(FriendshipDataForm.class,
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
}
