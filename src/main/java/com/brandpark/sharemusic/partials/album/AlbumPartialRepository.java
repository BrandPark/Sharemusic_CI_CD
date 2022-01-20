package com.brandpark.sharemusic.partials.album;

import com.brandpark.sharemusic.modules.account.account.domain.QAccount;
import com.brandpark.sharemusic.modules.account.follow.domain.QFollow;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.brandpark.sharemusic.modules.album.domain.QTrack;
import com.brandpark.sharemusic.modules.comment.domain.QComment;
import com.brandpark.sharemusic.modules.util.page.PagingDtoFactory;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.partials.album.form.AlbumCardForm;
import com.brandpark.sharemusic.partials.album.form.CommentInfoForm;
import com.brandpark.sharemusic.partials.album.form.TrackInfoForm;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.count;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class AlbumPartialRepository {

    private final JPAQueryFactory queryFactory;
    private QAccount account = QAccount.account;
    private QFollow follow = QFollow.follow;
    private QAlbum album = QAlbum.album;
    private QTrack track = QTrack.track;
    private QComment comment = QComment.comment;

    public PagingDto<AlbumCardForm> findAllAlbumCardsInfoAboutFollowings(Pageable pageable, Long myAccountId) {

        List<Long> targetIdList = queryFactory.select(follow.target.id)
                .from(follow)
                .where(follow.follower.id.eq(myAccountId))
                .fetch();

        targetIdList.add(myAccountId);

        QueryResults<AlbumCardForm> findAlbumQueryResults = getAllAlbumCardsInfo(pageable, isInFollowingIdList(targetIdList));

        return PagingDtoFactory.createPagingDto(findAlbumQueryResults.getResults(), pageable, findAlbumQueryResults.getTotal(), 10);
    }

    public PagingDto<AlbumCardForm> findAllAlbumCardsInfo(Pageable pageable) {

        QueryResults<AlbumCardForm> findAlbumsQueryResults = getAllAlbumCardsInfo(pageable, null);

        return PagingDtoFactory.createPagingDto(findAlbumsQueryResults.getResults(), pageable, findAlbumsQueryResults.getTotal(), 10);
    }

    public PagingDto<AlbumCardForm> findAllProfileAlbumCardsInfo(Pageable pageable, Long targetAccountId) {

        QueryResults<AlbumCardForm> queryResults = getAllAlbumCardsInfo(pageable, account.id.eq(targetAccountId));

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 10);
    }

    public PagingDto<AlbumCardForm> findAllAlbumsByAlbumName(Pageable pageable, String albumName) {

        QueryResults<AlbumCardForm> findAlbumsQueryResults = getAllAlbumCardsInfo(pageable, album.title.containsIgnoreCase(albumName));

        return PagingDtoFactory.createPagingDto(findAlbumsQueryResults.getResults(), pageable, findAlbumsQueryResults.getTotal(), 10);
    }

    public PagingDto<CommentInfoForm> findAllComments(Pageable pageable, Long albumId) {
        QueryResults<CommentInfoForm> queryResults = queryFactory.select(Projections.bean(CommentInfoForm.class,
                        comment.id,
                        account.nickname.as("writer"),
                        comment.content,
                        comment.createdDate,
                        comment.modifiedDate,
                        account.profileImage.as("writerProfileImage")
                ))
                .from(comment)
                .innerJoin(account).on(account.id.eq(comment.accountId))
                .where(comment.albumId.eq(albumId))
                .orderBy(comment.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 10);
    }

    private QueryResults<AlbumCardForm> getAllAlbumCardsInfo(Pageable pageable, BooleanExpression booleanExpression) {
        QueryResults<AlbumCardForm> findAlbumsQueryResults = queryFactory.select(Projections.fields(AlbumCardForm.class,
                        album.id.as("albumId"),
                        album.title,
                        album.description,
                        album.albumImage,
                        ExpressionUtils.as(
                                JPAExpressions.select(count(track.id))
                                        .from(track)
                                        .where(track.album.id.eq(album.id))
                                , "trackCount"
                        ),
                        account.nickname.as("creatorNickname"),
                        account.profileImage.as("creatorProfileImage"),
                        album.createdDate
                )).from(album)
                .innerJoin(account).on(album.accountId.eq(account.id))
                .where(booleanExpression)
                .orderBy(album.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        setTracks(findAlbumsQueryResults.getResults());

        return findAlbumsQueryResults;
    }

    private List<Long> getAlbumIdListByAlbums(List<AlbumCardForm> albumCardFormList) {
        return albumCardFormList
                .stream()
                .map(AlbumCardForm::getAlbumId)
                .collect(Collectors.toList());
    }

    private List<Long> getDistinctAlbumIdList(BooleanExpression booleanExpression) {
        return queryFactory.selectDistinct(
                        track.album.id
                )
                .from(track)
                .where(booleanExpression)
                .fetch();
    }

    private void setTracks(List<AlbumCardForm> albumCardFormList) {
        List<Long> albumIdList = getAlbumIdListByAlbums(albumCardFormList);

        Map<Long, List<TrackInfoForm>> tracksMap = queryFactory.select(Projections.fields(TrackInfoForm.class,
                        album.id.as("albumId"),
                        track.name,
                        track.artist
                )).from(track)
                .innerJoin(album).on(track.album.id.eq(album.id))
                .where(album.id.in(albumIdList))
                .fetch().stream()
                .collect(Collectors.groupingBy(TrackInfoForm::getAlbumId, Collectors.toList()));

        albumCardFormList.stream().forEach(albumCardForm -> {
            albumCardForm.setTracks(tracksMap.get(albumCardForm.getAlbumId()));
        });
    }

    private BooleanExpression isInFollowingIdList(List<Long> followingIdList) {
        if (followingIdList == null) {
            return null;
        }

        return album.accountId.in(followingIdList);
    }
}
