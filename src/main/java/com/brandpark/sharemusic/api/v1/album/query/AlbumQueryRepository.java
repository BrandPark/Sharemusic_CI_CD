package com.brandpark.sharemusic.api.v1.album.query;

import com.brandpark.sharemusic.modules.util.page.PagingDtoFactory;
import com.brandpark.sharemusic.api.SearchDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumDetailDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumShortDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.CommentDetailDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.TrackDetailDto;
import com.brandpark.sharemusic.api.v1.search.dto.AlbumSearchResult;
import com.brandpark.sharemusic.api.v1.search.dto.TrackSearchResult;
import com.brandpark.sharemusic.modules.util.page.dto.PagingDto;
import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.brandpark.sharemusic.modules.album.domain.QTrack;
import com.brandpark.sharemusic.modules.comment.domain.QComment;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class AlbumQueryRepository {

    private final JPAQueryFactory queryFactory;
    QAlbum album = QAlbum.album;
    QAccount account = QAccount.account;
    QTrack track = QTrack.track;
    QComment comment = QComment.comment;

    public PagingDto<AlbumShortDto> findAllAlbumsByAccountIdList(Pageable pageable, SearchDto searchDto) {

        QueryResults<AlbumShortDto> queryResults = queryFactory.select(
                        Projections.bean(AlbumShortDto.class,
                                album.id.as("albumId"),
                                album.title,
                                album.albumImage,
                                album.description,
                                album.trackCount,
                                account.nickname.as("creatorNickname"),
                                account.profileImage.as("creatorProfileImage"),
                                album.createdDate
                        ))
                .from(album)
                .innerJoin(account).on(album.accountId.eq(account.id))
                .where(searchCondition(searchDto))
                .orderBy(album.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 10);
    }

    public AlbumDetailDto findAlbumDetailDtoById(Long albumId) {

        AlbumDetailDto albumDetailDto = queryFactory.select(
                        Projections.bean(AlbumDetailDto.class,
                                album.id,
                                album.title,
                                album.albumImage,
                                album.description,
                                album.createdDate,
                                album.modifiedDate,
                                account.nickname.as("creator"),
                                account.profileImage.as("creatorProfileImage")
                        )
                ).from(album)
                .innerJoin(account).on(album.accountId.eq(account.id))
                .where(album.id.eq(albumId))
                .fetchOne();

        List<TrackDetailDto> trackDetailDtos = queryFactory.select(Projections.bean(TrackDetailDto.class,
                        track.id,
                        track.name,
                        track.artist))
                .from(track)
                .where(track.album.id.eq(albumId))
                .fetch();

        albumDetailDto.setTracks(trackDetailDtos);

        return albumDetailDto;
    }

    public PagingDto<CommentDetailDto> findAllCommentDetailDtoByAlbumId(Long albumId, Pageable pageable) {

        QueryResults<CommentDetailDto> queryResults = queryFactory.select(Projections.bean(CommentDetailDto.class,
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

    public PagingDto<AlbumSearchResult> findAllAlbumsByAlbumName(String albumName, Pageable pageable) {
        QueryResults<AlbumSearchResult> queryResults = queryFactory.select(Projections.fields(AlbumSearchResult.class,
                        album.id.as("albumId"),
                        album.title,
                        album.description,
                        album.trackCount,
                        account.nickname.as("creatorNickname"),
                        account.profileImage.as("creatorProfileImage"),
                        album.createdDate
                ))
                .from(album)
                .innerJoin(account).on(album.accountId.eq(account.id))
                .where(
                        album.title.containsIgnoreCase(albumName)
                )
                .orderBy(album.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        PagingDto<AlbumSearchResult> page = PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 10);
        for (AlbumSearchResult albumInfo : page.getContents()) {
            albumInfo.setTracks(findAllTracksByAlbumId(albumInfo.getAlbumId()));
        }

        return page;
    }

    public PagingDto<AlbumSearchResult> findAllAlbumsByTrackName(String trackName, Pageable pageable) {
        List<Long> albumIds = queryFactory.selectDistinct(
                        track.album.id
                )
                .from(track)
                .where(track.name.containsIgnoreCase(trackName))
                .fetch();

        QueryResults<AlbumSearchResult> queryResults = queryFactory.select(Projections.fields(AlbumSearchResult.class,
                        album.id.as("albumId"),
                        album.title,
                        album.description,
                        album.trackCount,
                        account.nickname.as("creatorNickname"),
                        account.profileImage.as("creatorProfileImage"),
                        album.createdDate
                ))
                .from(album)
                .innerJoin(account).on(album.accountId.eq(account.id))
                .where(album.id.in(albumIds))
                .orderBy(album.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        PagingDto<AlbumSearchResult> page = PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 10);
        for (AlbumSearchResult albumInfo : page.getContents()) {
            albumInfo.setTracks(findAllTracksByAlbumId(albumInfo.getAlbumId()));
        }

        return page;
    }

    public PagingDto<AlbumSearchResult> findAllAlbumsByTrackArtist(String trackArtist, Pageable pageable) {
        List<Long> albumIds = queryFactory.selectDistinct(
                        track.album.id
                )
                .from(track)
                .where(track.artist.containsIgnoreCase(trackArtist))
                .fetch();

        QueryResults<AlbumSearchResult> queryResults = queryFactory.select(Projections.fields(AlbumSearchResult.class,
                        album.id.as("albumId"),
                        album.title,
                        album.description,
                        album.trackCount,
                        account.nickname.as("creatorNickname"),
                        account.profileImage.as("creatorProfileImage"),
                        album.createdDate
                ))
                .from(album)
                .innerJoin(account).on(album.accountId.eq(account.id))
                .where(album.id.in(albumIds))
                .orderBy(album.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        PagingDto<AlbumSearchResult> page = PagingDtoFactory.createPagingDto(queryResults.getResults(), pageable, queryResults.getTotal(), 10);
        for (AlbumSearchResult albumInfo : page.getContents()) {
            albumInfo.setTracks(findAllTracksByAlbumId(albumInfo.getAlbumId()));
        }

        return page;
    }

    private List<TrackSearchResult> findAllTracksByAlbumId(Long albumId) {
        return queryFactory.select(Projections.fields(TrackSearchResult.class,
                        track.id.as("trackId"),
                        track.name,
                        track.artist
                ))
                .from(track)
                .where(track.album.id.eq(albumId))
                .orderBy(track.name.asc())
                .fetch();
    }

    private BooleanExpression searchCondition(SearchDto searchDto) {
        if (searchDto.getQ() == null) {
            return null;
        }

        return account.nickname.eq(searchDto.getQ());
    }
}
