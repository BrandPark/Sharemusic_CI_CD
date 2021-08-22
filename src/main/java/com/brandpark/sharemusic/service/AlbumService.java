package com.brandpark.sharemusic.service;

import com.brandpark.sharemusic.domain.album.AlbumRepository;
import com.brandpark.sharemusic.web.api.dto.AlbumSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    @Transactional
    public Long saveAlbum(AlbumSaveRequestDto requestDto) {
        return albumRepository.save(requestDto.toEntity()).getId();
    }
}
