package com.brandpark.sharemusic.modules.album.query;

import com.brandpark.sharemusic.modules.account.domain.QAccount;
import com.brandpark.sharemusic.modules.album.domain.QAlbum;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class AlbumQueryRepository {

    private final JPAQueryFactory query;

    public List<AlbumShortDto> findAllAlbumShortDto() {
        QAlbum album = QAlbum.album;
        QAccount account = QAccount.account;

        return query.select(
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
                .fetch();
    }

}
