package com.brandpark.sharemusic.api.v1.album.query;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse.TrackInfoResponse;
import com.brandpark.sharemusic.api.v1.album.query.dto.AlbumDetailDto;
import com.brandpark.sharemusic.api.v1.album.query.dto.TrackDetailDto;
import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.brandpark.sharemusic.modules.album.domain.QTrack;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class AlbumQueryRepository {

    private final JPAQueryFactory queryFactory;
    QAlbum album = QAlbum.album;
    QAccount account = QAccount.account;
    QTrack track = QTrack.track;

    public PageResult<AlbumInfoResponse> findAllAlbumsInfo(Pageable pageable) {

        QueryResults<AlbumInfoResponse> queryResults = getAllAlbumsInfo(pageable);

        return PageResultFactory.createPageResult(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    private QueryResults<AlbumInfoResponse> getAllAlbumsInfo(Pageable pageable) {
        QueryResults<AlbumInfoResponse> queryResults = queryFactory.select(
                        Projections.bean(AlbumInfoResponse.class,
                                album.id.as("albumId"),
                                album.title,
                                album.albumImage,
                                album.description,
                                album.trackCount,
                                album.accountId,
                                album.createdDate,
                                album.modifiedDate
                        ))
                .from(album)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        setTrackInfo(queryResults);

        return queryResults;
    }

    private void setTrackInfo(QueryResults<AlbumInfoResponse> queryResults) {
        List<Long> albumIdList = queryResults.getResults().stream()
                .map(AlbumInfoResponse::getAlbumId)
                .collect(Collectors.toList());

        Map<Long, List<TrackInfoResponse>> trackMap = queryFactory.from(track)
                .where(track.album.id.in(albumIdList))
                .transform(
                        groupBy(track.album.id).as(list(Projections.fields(
                                TrackInfoResponse.class,
                                track.id.as("trackId"),
                                track.name,
                                track.artist))
                        )
                );

        queryResults.getResults().stream()
                .forEach(album -> {
                    album.setTracks(trackMap.get(album.getAlbumId()));
                });
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
}
