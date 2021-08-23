package com.brandpark.sharemusic.service;

import com.brandpark.sharemusic.domain.album.Album;
import com.brandpark.sharemusic.domain.album.AlbumRepository;
import com.brandpark.sharemusic.domain.album.TrackRepository;
import com.brandpark.sharemusic.exception.NotFoundAlbumException;
import com.brandpark.sharemusic.web.api.dto.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.api.dto.AlbumUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;

    @Transactional
    public Long saveAlbum(AlbumSaveRequestDto requestDto) {
        return albumRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public void updateAlbum(Long albumId, AlbumUpdateRequestDto dto) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundAlbumException("존재하지 않는 앨범입니다."));

        album.update(dto.getTitle(), dto.getImgUrl(), dto.getThemes());

        List<AlbumUpdateRequestDto.TrackDto> tracks = dto.getTracks();
        // TODO : track들 변경
    }

    @Transactional
    public void deleteAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundAlbumException("존재하지 않는 앨범입니다."));

        albumRepository.deleteById(albumId);
    }
}
