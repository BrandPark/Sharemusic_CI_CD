package com.brandpark.sharemusic.api.album.query;

import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class AlbumQueryRepository {

    private final JPAQueryFactory query;

    public Page<AlbumShortDto> findAllAlbumShortDto(Pageable pageable) {
        QAlbum album = QAlbum.album;
        QAccount account = QAccount.account;

        QueryResults<AlbumShortDto> result = query.select(
                        Projections.bean(AlbumShortDto.class,
                                album.title,
                                album.albumImage,
                                album.description.as("description"),
                                album.trackCount,
                                account.nickname.as("creator"),
                                account.profileImage.as("creatorProfileImage")
                        ))
                .from(album)
                .innerJoin(account).on(album.accountId.eq(account.id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

}
