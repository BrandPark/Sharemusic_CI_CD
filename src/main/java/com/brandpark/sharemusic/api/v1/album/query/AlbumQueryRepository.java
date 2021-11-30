package com.brandpark.sharemusic.api.v1.album.query;

import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumInfoResponse.TrackInfoResponse;
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
}
