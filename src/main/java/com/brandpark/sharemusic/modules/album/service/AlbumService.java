package com.brandpark.sharemusic.modules.album.service;

import com.brandpark.sharemusic.api.v1.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackUpdateRequest;
import com.brandpark.sharemusic.modules.MyUtil;
import com.brandpark.sharemusic.modules.album.domain.Album;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.album.domain.Track;
import com.brandpark.sharemusic.modules.album.form.AlbumUpdateForm;
import com.brandpark.sharemusic.modules.event.CreateAlbumEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AlbumService {

    private final ModelMapper modelMapper;
    private final AlbumRepository albumRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AlbumUpdateForm entityToForm(Album album) {
        return modelMapper.map(album, AlbumUpdateForm.class);
    }

    @Transactional
    public Long saveAlbum(Long accountId, AlbumSaveRequest requestDto) {

        requestDto.setDescription(MyUtil.toBrTag(requestDto.getDescription()));

        Long albumId = albumRepository.save(requestDto.toEntity(accountId)).getId();

        eventPublisher.publishEvent(CreateAlbumEvent.builder()
                .albumId(albumId)
                .creatorId(accountId)
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
}

