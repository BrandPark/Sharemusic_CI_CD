package com.brandpark.sharemusic.modules.album.service;

import com.brandpark.sharemusic.api.v1.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackUpdateRequest;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.album.domain.*;
import com.brandpark.sharemusic.modules.album.dto.CreateAlbumDto;
import com.brandpark.sharemusic.modules.album.dto.UpdateAlbumDto;
import com.brandpark.sharemusic.modules.album.form.AlbumUpdateForm;
import com.brandpark.sharemusic.modules.event.CreateAlbumEvent;
import com.brandpark.sharemusic.modules.util.MyUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final ModelMapper modelMapper;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AlbumUpdateForm entityToForm(Album album) {
        return modelMapper.map(album, AlbumUpdateForm.class);
    }

    public Long createAlbum(CreateAlbumDto data, SessionAccount loginAccount) {
        Long albumId = albumRepository.save(data.toEntity(loginAccount.getId())).getId();

        eventPublisher.publishEvent(CreateAlbumEvent.builder()
                .albumId(albumId)
                .creatorId(loginAccount.getId())
                .build());

        return albumId;
    }

    @Transactional
    public void updateAlbum(AlbumUpdateRequest requestDto, Album album) {
        // 앨범 정보 변경
        requestDto.setDescription(MyUtil.toBrTag(requestDto.getDescription()));
        album.updateAlbum(requestDto.getTitle(), requestDto.getAlbumImage(), requestDto.getDescription());

        // 트랙들 순회하며 변경
        Map<Long, Track> lookupTracksMap = album.getTracks().stream().collect(Collectors.toMap(Track::getId, Function.identity()));
        for (TrackUpdateRequest dto : requestDto.getTracks()) {
            if (dto.getId() == null) {  // 신규
                Track newTrack = modelMapper.map(dto, Track.class);

                album.addTrack(newTrack);
                lookupTracksMap.remove(dto.getId());
            }
            else {    // 보존 or update
                Track track = lookupTracksMap.get(dto.getId());
                modelMapper.map(dto, track);
                lookupTracksMap.remove(dto.getId());
            }
        }

        if (!lookupTracksMap.isEmpty()) {   // 삭제된 것
            for (Map.Entry<Long, Track> entry : lookupTracksMap.entrySet()) {
                album.removeTrack(entry.getValue());
            }
        }
     }

    @Transactional
    public void updateAlbum(UpdateAlbumDto data, Long albumId) {
        Album album = albumRepository.findById(albumId).get();

        album.updateAlbum(data.getTitle(), data.getAlbumImage(), data.getDescription());

        Map<Long, Track> lookupTrackMap = album.getTracks().stream()
                .collect(Collectors.toMap(Track::getId, Function.identity()));

        List<Track> insertTrackList = new ArrayList<>();
        List<Track> updateTrackList = new ArrayList<>();
        List<Track> removeTrackList = new ArrayList<>();

        for (UpdateAlbumDto.UpdateTrackDto tData : data.getTracks()) {

            TrackStatus trackStatus = tData.getStatus();

            switch (trackStatus) {
                case NONE: break;
                case INSERT:
                    insertTrackList.add(lookupTrackMap.get(tData.getId()));
                    break;
                case UPDATE:
                    updateTrackList.add(lookupTrackMap.get(tData.getId()));
                    break;
                case REMOVE:
                    removeTrackList.add(lookupTrackMap.get(tData.getId()));
                    break;
            }

            trackRepository.batchInsert(insertTrackList);



        }


    }
}

