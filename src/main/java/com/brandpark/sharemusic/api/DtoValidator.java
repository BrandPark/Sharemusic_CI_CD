package com.brandpark.sharemusic.api;

import com.brandpark.sharemusic.api.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.album.dto.TrackUpdateRequest;
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

        List<TrackSaveRequest> tracks = requestDto.getTracks();

        checkTitle(requestDto.getTitle());
        checkTrackCount(tracks.size());
        checkAllTrackSaveDto(tracks);
    }

    public void validateAlbumUpdateDto(AlbumUpdateRequest requestDto) {

        List<TrackUpdateRequest> tracks = requestDto.getTracks();

        checkTitle(requestDto.getTitle());
        checkTrackCount(tracks.size());
        checkAllTrackUpdateDto(tracks);
    }

    private void checkAllTrackUpdateDto(List<TrackUpdateRequest> tracks) {
        for (TrackUpdateRequest track : tracks) {
            checkTrackInfo(track.getName(), track.getArtist());
        }
    }

    private void checkAllTrackSaveDto(List<TrackSaveRequest> tracks) {
        for (TrackSaveRequest track : tracks) {
            checkTrackInfo(track.getName(), track.getArtist());
        }
    }

    private void checkTrackInfo(String name, String artist) {
        if (!hasText(name) || !hasText(artist)) {
            throw new ApiException(BLANK_FIELD_EXCEPTION, "'tracks' 의 요소에 'name' 또는 'artist' 가 비어있습니다.");
        }
    }

    private void checkTrackCount(int count) {
        if (count > 5 || count < 1) {
            throw new ApiException(INVALID_TRACKS_COUNT_EXCEPTION, "tracks 의 요소는 1개 이상 5개 이하여야 합니다.");
        }
    }

    private void checkTitle(String title) {
        if (!hasText(title)) {
            throw new ApiException(BLANK_FIELD_EXCEPTION, "'title' 이 비어있습니다.");
        }
    }

    private boolean hasText(String name) {
        return StringUtils.hasText(name);
    }


}
