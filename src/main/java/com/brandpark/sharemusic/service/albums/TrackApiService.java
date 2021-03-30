package com.brandpark.sharemusic.service.albums;

import com.brandpark.sharemusic.domain.tracks.TrackRepository;
import com.brandpark.sharemusic.web.dto.TrackSaveRequestDto;
import com.brandpark.sharemusic.web.dto.TrackUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TrackApiService {

    private final TrackRepository trackRepository;

    @Transactional
    public Long save(TrackSaveRequestDto requestDto) {
        return trackRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, TrackUpdateRequestDto requestDto) {

    }
}
