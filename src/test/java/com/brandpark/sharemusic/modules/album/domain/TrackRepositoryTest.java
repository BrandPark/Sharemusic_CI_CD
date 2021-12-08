package com.brandpark.sharemusic.modules.album.domain;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.testUtils.AlbumFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class TrackRepositoryTest {

    @Autowired TrackRepository trackRepository;
    @Autowired AlbumFactory albumFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired AlbumRepository albumRepository;
    @Autowired EntityManager entityManager;

    @DisplayName("Track Batch Insert")
    @Test
    public void TrackBatchInsert() throws Exception {

        // given
        Account account = accountFactory.persistAccount("account");
        Long albumId = albumFactory.persistAlbumWithTracks("title", 0, account.getId()).getId();
        int trackCount = 10;

        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < trackCount; i++) {
            tracks.add(Track.createTrack("트랙이름" + i, "트랙아티스트" + i));
        }

        // when
        System.out.println("===============시작================");
        int insertCount = trackRepository.batchInsert(tracks, albumId);
        System.out.println("===============끝================");

        // then
        assertThat(insertCount).isEqualTo(trackCount);

        List<Track> all = trackRepository.findAll();
        assertThat(all.size()).isEqualTo(insertCount);
    }

    @DisplayName("Track Batch Update")
    @Test
    public void TrackBatchUpdate() throws Exception {

        // given
        int trackCount = 10;

        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < trackCount; i++) {
            tracks.add(Track.createTrack("트랙이름" + i, "트랙아티스트" + i));
        }

        trackRepository.saveAll(tracks);

        // when
        for (int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(i);

            track.updateTrack("modifiedName" + i, "modifiedArtist" + i);
        }
        entityManager.clear();

        System.out.println("===============시작================");
        int updateCount = trackRepository.batchUpdate(tracks);
        System.out.println("===============끝================");

        // then
        assertThat(updateCount).isEqualTo(trackCount);

        List<Track> all = trackRepository.findAll();
        assertThat(all.size()).isEqualTo(updateCount);
        for (int i = 0; i < all.size(); i++) {
            assertThat(all.get(i).getName()).contains("modified");
            assertThat(all.get(i).getArtist()).contains("modified");
        }
    }

    @DisplayName("Track Batch Remove")
    @Test
    public void TrackBatchRemove() throws Exception {

        // given
        int trackCount = 10;

        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < trackCount; i++) {
            tracks.add(Track.createTrack("트랙이름" + i, "트랙아티스트" + i));
        }

        trackRepository.saveAll(tracks);

        // when
        System.out.println("===============시작================");
        int removeCount = trackRepository.batchDelete(tracks);
        System.out.println("===============끝================");

        // then
        assertThat(removeCount).isEqualTo(trackCount);

        List<Track> all = trackRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }

    @DisplayName("albumId와 trackId를 통해 해당 트랙이 존재하는지 확인 - 참(해당 앨범에 해당 트랙이 존재하는 경우)")
    @Test
    public void ExistsTrackByIdAndAlbumId_True_When_AllExists() throws Exception {

        // given
        Account albumCreator = accountFactory.persistAccount("albumCreator");
        Album existsAlbum = albumFactory.persistAlbumWithTracks("앨범", 1, albumCreator.getId());
        Track existsTrack = existsAlbum.getTracks().get(0);

        // when
        boolean exists = trackRepository.existsTrackByIdAndAlbumId(existsTrack.getId(), existsAlbum.getId());

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("albumId와 trackId를 통해 해당 트랙이 존재하는지 확인 - 거짓(트랙이 존재하지 않는 경우)")
    @Test
    public void ExistsTrackByIdAndAlbumId_False_When_TrackNotExists() throws Exception {

        // given
        Account albumCreator = accountFactory.persistAccount("albumCreator");
        Album existsAlbum = albumFactory.persistAlbumWithTracks("앨범", 1, albumCreator.getId());

        Long notExistsTrackId = 9999L;
        boolean notExistsTrack = !trackRepository.existsById(notExistsTrackId);

        assertThat(notExistsTrack).isTrue();

        // when
        boolean exists = trackRepository.existsTrackByIdAndAlbumId(notExistsTrackId, existsAlbum.getId());

        // then
        assertThat(exists).isFalse();
    }

    @DisplayName("albumId와 trackId를 통해 해당 트랙이 존재하는지 확인 - 거짓(앨범이 존재하지 않는 경우)")
    @Test
    public void ExistsTrackByIdAndAlbumId_False_When_AlbumNotExists() throws Exception {

        // given
        Account albumCreator = accountFactory.persistAccount("albumCreator");
        Track existsTrack = albumFactory.persistAlbumWithTracks("앨범", 1, albumCreator.getId()).getTracks().get(0);

        Long notExistsAlbumId = 999L;
        boolean notExistsAlbum = !albumRepository.existsById(notExistsAlbumId);

        assertThat(notExistsAlbum).isTrue();

        // when
        boolean exists = trackRepository.existsTrackByIdAndAlbumId(existsTrack.getId(), notExistsAlbumId);

        // then
        assertThat(exists).isFalse();
    }
}