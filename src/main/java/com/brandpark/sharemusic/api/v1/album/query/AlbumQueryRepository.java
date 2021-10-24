package com.brandpark.sharemusic.api.v1.album.query;

import com.brandpark.sharemusic.api.v1.album.dto.AlbumListPagingDto;
import com.brandpark.sharemusic.api.v1.album.dto.CommentListPagingDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumDetailDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumShortDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.CommentDetailDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.TrackDetailDto;
import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.brandpark.sharemusic.modules.album.domain.QTrack;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import com.brandpark.sharemusic.modules.comment.domain.QComment;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class AlbumQueryRepository {

    private final JPAQueryFactory query;
    private final CommentRepository commentRepository;
    QAlbum album = QAlbum.album;
    QAccount account = QAccount.account;
    QTrack track = QTrack.track;
    QComment comment = QComment.comment;

    public AlbumListPagingDto findAllAlbumShortDto(Pageable pageable) {

        QueryResults<AlbumShortDto> queryResults = query.select(
                        Projections.bean(AlbumShortDto.class,
                                album.id,
                                album.title,
                                album.albumImage,
                                album.description,
                                album.trackCount,
                                account.nickname.as("creator"),
                                account.profileImage.as("creatorProfileImage"),
                                album.createDate
                        ))
                .from(album)
                .orderBy(album.createDate.desc())
                .innerJoin(account).on(album.accountId.eq(account.id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        PageImpl<AlbumShortDto> albumPage = new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());

        AlbumListPagingDto result = new AlbumListPagingDto();
        result.setAlbums(albumPage.getContent());
        result.setTotalPages(albumPage.getTotalPages());
        result.setTotalElements(albumPage.getTotalElements());
        result.setPageNumber(albumPage.getNumber());
        result.setNumberOfElements(albumPage.getNumberOfElements());
        result.setOffset(pageable.getOffset());
        result.setPageSize(albumPage.getSize());

        return result;
    }

    public AlbumDetailDto findAlbumDetailDtoById(Long albumId) {

        AlbumDetailDto albumDetailDto = query.select(
                        Projections.bean(AlbumDetailDto.class,
                                album.id,
                                album.title,
                                album.albumImage,
                                album.description,
                                album.createDate,
                                album.modifiedDate,
                                account.nickname.as("creator"),
                                account.profileImage.as("creatorProfileImage")
                        )
                ).from(album)
                .innerJoin(account).on(album.accountId.eq(account.id))
                .where(album.id.eq(albumId))
                .fetchOne();

        List<TrackDetailDto> trackDetailDtos = query.select(Projections.bean(TrackDetailDto.class,
                        track.id,
                        track.name,
                        track.artist))
                .from(track)
                .where(track.album.id.eq(albumId))
                .fetch();

        albumDetailDto.setTracks(trackDetailDtos);

        return albumDetailDto;
    }

    public CommentListPagingDto findAllCommentDetailDtoByAlbumId(Long albumId, Pageable pageable) {

        QueryResults<CommentDetailDto> queryResults = query.select(Projections.bean(CommentDetailDto.class,
                        comment.id,
                        account.nickname.as("writer"),
                        comment.content,
                        comment.createDate,
                        comment.modifiedDate,
                        account.profileImage.as("writerProfileImage")
                ))
                .from(comment)
                .innerJoin(account).on(account.id.eq(comment.accountId))
                .where(comment.albumId.eq(albumId))
                .orderBy(comment.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        PageImpl<CommentDetailDto> commentPage = new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());

        CommentListPagingDto result = new CommentListPagingDto();
        result.setComments(queryResults.getResults());
        result.setTotalPages(commentPage.getTotalPages());
        result.setTotalElements(commentPage.getTotalElements());
        result.setPageNumber(commentPage.getNumber());
        result.setNumberOfElements(commentPage.getNumberOfElements());
        result.setOffset(pageable.getOffset());
        result.setPageSize(commentPage.getSize());

        return result;
    }
}
