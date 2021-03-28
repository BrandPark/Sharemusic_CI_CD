package com.brandpark.sharemusic.service.albums;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.albums.AlbumRepository;
import com.brandpark.sharemusic.web.dto.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.dto.AlbumUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class AlbumApiService {

    private final AlbumRepository albumRepository;

    @Transactional
    public Long save(AlbumSaveRequestDto requestDto) {
        return albumRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, AlbumUpdateRequestDto requestDto) {
        Album saved = albumRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        String name = requestDto.getName();
        saved.update(name);

        return id;
    }
}
