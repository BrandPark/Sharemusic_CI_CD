package com.brandpark.sharemusic.modules.album.service;

import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.album.domain.*;
import com.brandpark.sharemusic.modules.album.dto.CreateAlbumDto;
import com.brandpark.sharemusic.modules.album.dto.UpdateAlbumDto;
import com.brandpark.sharemusic.modules.album.form.AlbumDetailInfoForm;
import com.brandpark.sharemusic.modules.album.form.AlbumUpdateForm;
import com.brandpark.sharemusic.modules.comment.domain.CommentRepository;
import com.brandpark.sharemusic.modules.event.CreateAlbumEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.brandpark.sharemusic.modules.album.domain.TrackStatus.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;
    private final CommentRepository commentRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Long createAlbum(CreateAlbumDto data, SessionAccount loginAccount) {

        Long albumId = albumRepository.save(data.toEntity(loginAccount.getId())).getId();

        eventPublisher.publishEvent(CreateAlbumEvent.builder()
                .albumId(albumId)
                .creatorId(loginAccount.getId())
                .build());

        return albumId;
    }

    @Transactional
    public void updateAlbum(UpdateAlbumDto data, Long albumId) {
        Album album = albumRepository.findById(albumId).get();

        album.updateAlbum(data.getTitle(), data.getAlbumImage(), data.getDescription());
        entityManager.flush();

        Map<Long, Track> lookupTrackMap = album.getTracks().stream()
                .collect(Collectors.toMap(Track::getId, Function.identity()));

        Map<TrackStatus, List<Track>> tracksGroupByStatus = new HashMap<>();
        for (TrackStatus status : TrackStatus.values()) {
            tracksGroupByStatus.put(status, new ArrayList<>());
        }

        data.getTracks().stream()
                .forEach(tData -> {
                    Track track = tData.getStatus() == INSERT
                            ? Track.createTrack(tData.getName(), tData.getArtist())
                            : lookupTrackMap.get(tData.getId());

                    track.updateTrack(tData.getName(), tData.getArtist());
                    tracksGroupByStatus.get(tData.getStatus()).add(track);
                });

        trackRepository.batchInsert(tracksGroupByStatus.get(INSERT), albumId);
        trackRepository.batchUpdate(tracksGroupByStatus.get(UPDATE));
        trackRepository.batchRemove(tracksGroupByStatus.get(REMOVE));

        entityManager.clear();
    }

    @Transactional
    public void deleteAlbum(Long albumId) {
        commentRepository.deleteAllCommentsByAlbumId(albumId);
        entityManager.clear();

        albumRepository.deleteById(albumId);
    }

    public AlbumDetailInfoForm getAlbumDetailForm(Long albumId) {

        Album album = albumRepository.findById(albumId).get();
        Account account = accountRepository.findById(album.getAccountId()).get();

        return new AlbumDetailInfoForm(album, account);
    }

    public AlbumUpdateForm getAlbumUpdateForm(Long albumId) {
        Album album = albumRepository.findById(albumId).get();

        return new AlbumUpdateForm(album);
    }
}

