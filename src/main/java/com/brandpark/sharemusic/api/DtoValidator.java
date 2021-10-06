package com.brandpark.sharemusic.api;

import com.brandpark.sharemusic.api.exception.ApiException;
import com.brandpark.sharemusic.api.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.album.dto.TrackSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.brandpark.sharemusic.api.exception.Error.BLANK_FIELD_EXCEPTION;
import static com.brandpark.sharemusic.api.exception.Error.INVALID_TRACKS_COUNT_EXCEPTION;

@RequiredArgsConstructor
@Component
public class DtoValidator {

    public void validateAlbumSaveDto(AlbumSaveRequest requestDto) {

        if (!StringUtils.hasText(requestDto.getTitle())) {
            throw new ApiException(BLANK_FIELD_EXCEPTION, "'title' 이 비어있습니다.");
        }

        List<TrackSaveRequest> tracks = requestDto.getTracks();
        if (tracks.size() > 5 || tracks.size() < 1) {
            throw new ApiException(INVALID_TRACKS_COUNT_EXCEPTION, "tracks 의 요소는 1개 이상 5개 이하여야 합니다.");
        }

        for (TrackSaveRequest track : tracks) {
            if (!StringUtils.hasText(track.getName()) || !StringUtils.hasText(track.getArtist())) {
                throw new ApiException(BLANK_FIELD_EXCEPTION, "'tracks' 의 요소에 'name' 또는 'artist' 가 비어있습니다.");
            }
        }
    }
}
