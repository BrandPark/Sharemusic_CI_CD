package com.brandpark.sharemusic.service.albums;

import com.brandpark.sharemusic.domain.albums.Album;
import com.brandpark.sharemusic.domain.albums.AlbumRepository;
import com.brandpark.sharemusic.web.dto.albums.AlbumListResponseDto;
import com.brandpark.sharemusic.web.dto.albums.AlbumResponseDto;
import com.brandpark.sharemusic.web.dto.albums.AlbumSaveRequestDto;
import com.brandpark.sharemusic.web.dto.albums.AlbumUpdateRequestDto;
import com.brandpark.sharemusic.web.dto.tracks.TrackUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


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
        //Persist context에서 앨범과 Track들을 가져온다.
        Album album = albumRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 앨범이 없습니다. id=" + id));

        album.update(requestDto.getName(), requestDto.getTracks());
        return id;
    }

    @Transactional
    public Long delete(Long id) {
        Album saved = albumRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        albumRepository.delete(saved);

        return id;
    }

    @Transactional(readOnly = true)
    public List<AlbumListResponseDto> findAllDesc() {
        return albumRepository.findAllDesc().stream().map(AlbumListResponseDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AlbumResponseDto findById(Long id) {
        Album saved = albumRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 앨범이 없습니다. id=" + id));

        return new AlbumResponseDto(saved);
    }

}
