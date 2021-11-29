package com.brandpark.sharemusic.api.v1.search.query;

import com.brandpark.sharemusic.api.QueryRepository;
import com.brandpark.sharemusic.api.page.PageResult;
import com.brandpark.sharemusic.api.page.PageResultFactory;
import com.brandpark.sharemusic.api.v1.search.dto.AccountSearchResult;
import com.brandpark.sharemusic.api.v1.search.dto.AlbumSearchResult;
import com.brandpark.sharemusic.api.v1.search.dto.AlbumSearchResult.TrackSearchResult;
import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.brandpark.sharemusic.modules.album.domain.QTrack;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@RequiredArgsConstructor
@QueryRepository
public class SearchQueryRepository {

    private final JPAQueryFactory queryFactory;
    private QAccount account = QAccount.account;
    private QAlbum album = QAlbum.album;
    private QTrack track = QTrack.track;

    public PageResult<AccountSearchResult> findAllAccountSearchResultsByNameOrNickname(Pageable pageable, String nameOrNickname) {
        QueryResults<AccountSearchResult> queryResults = queryFactory.select(Projections.fields(AccountSearchResult.class,
                        account.id.as("accountId"),
                        account.email,
                        account.name,
                        account.nickname,
                        account.bio,
                        account.profileImage,
                        account.role,
                        account.createdDate
                ))
                .from(account)
                .where(
                        account.name.containsIgnoreCase(nameOrNickname)
                                .or(account.nickname.containsIgnoreCase(nameOrNickname))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return PageResultFactory.createPageResult(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    public PageResult<AlbumSearchResult> findAllAlbumSearchResultsByTitle(Pageable pageable, String title) {
        BooleanExpression booleanExpression = album.title.containsIgnoreCase(title);

        QueryResults<AlbumSearchResult> queryResults = getAllAlbumSearchResultsWithoutTrack(pageable, booleanExpression);

        setTrackInfo(queryResults, null);

        return PageResultFactory.createPageResult(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    public PageResult<AlbumSearchResult> findAllAlbumSearchResultsByTrackName(Pageable pageable, String trackName) {
        BooleanExpression booleanExpression = track.name.containsIgnoreCase(trackName);

        QueryResults<AlbumSearchResult> queryResults = getAllAlbumSearchResultsWithoutTrack(pageable, null);

        setTrackInfo(queryResults, booleanExpression);

        return PageResultFactory.createPageResult(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    public PageResult<AlbumSearchResult> findAllAlbumSearchResultsByTrackArtist(Pageable pageable, String trackArtist) {
        BooleanExpression booleanExpression = track.artist.containsIgnoreCase(trackArtist);

        QueryResults<AlbumSearchResult> queryResults = getAllAlbumSearchResultsWithoutTrack(pageable, null);

        setTrackInfo(queryResults, booleanExpression);

        return PageResultFactory.createPageResult(queryResults.getResults(), pageable, queryResults.getTotal());
    }

    private QueryResults<AlbumSearchResult> getAllAlbumSearchResultsWithoutTrack(Pageable pageable, BooleanExpression booleanExpression) {
        QueryResults<AlbumSearchResult> queryResults = queryFactory.select(
                        Projections.bean(AlbumSearchResult.class,
                                album.id.as("albumId"),
                                album.title,
                                album.description,
                                album.albumImage,
                                album.trackCount,
                                album.accountId,
                                album.createdDate,
                                album.modifiedDate
                        ))
                .from(album)
                .where(booleanExpression)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return queryResults;
    }

    private void setTrackInfo(QueryResults<AlbumSearchResult> queryResults, BooleanExpression booleanExpression) {
        List<Long> albumIdList = queryResults.getResults().stream()
                .map(AlbumSearchResult::getAlbumId)
                .collect(Collectors.toList());

        Map<Long, List<TrackSearchResult>> trackMap = queryFactory.from(track)
                .where(
                        track.album.id.in(albumIdList),
                        booleanExpression
                )
                .transform(
                        groupBy(track.album.id).as(list(Projections.fields(
                                TrackSearchResult.class,
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
