package com.brandpark.sharemusic.api.v1;

import com.brandpark.sharemusic.api.v1.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackUpdateRequest;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DtoValidator {

    private final AlbumRepository albumRepository;

    public void validateAlbumSaveDto(AlbumSaveRequest requestDto, Long accountId) {

        List<TrackSaveRequest> tracks = requestDto.getTracks();

        checkTitle(requestDto, accountId);
        checkDuplicateSaveTrack(tracks);
        checkTrackCount(tracks.size());
        checkAllTrackSaveDto(tracks);
    }

    public void validateAlbumUpdateDto(AlbumUpdateRequest requestDto, Long accountId, Long albumId) {

        List<TrackUpdateRequest> tracks = requestDto.getTracks();

        checkTitle(requestDto, accountId, albumId);
        checkDuplicateUpdateTrack(tracks);
        checkTrackCount(tracks.size());
        checkAllTrackUpdateDto(tracks);
    }

    private void checkDuplicateSaveTrack(List<TrackSaveRequest> tracks) {
        List<TrackSaveRequest> distinctTracks = tracks.stream().distinct().collect(Collectors.toList());
        if (distinctTracks.size() != tracks.size()) {
            throw new ApiException(Error.DUPLICATE_TRACK_EXCEPTION);
        }
    }

    private void checkDuplicateUpdateTrack(List<TrackUpdateRequest> tracks) {
        List<TrackUpdateRequest> distinctTracks = tracks.stream().distinct().collect(Collectors.toList());
        if (distinctTracks.size() != tracks.size()) {
            throw new ApiException(Error.DUPLICATE_TRACK_EXCEPTION);
        }
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
            throw new ApiException(Error.BLANK_FIELD_EXCEPTION, "'tracks' 의 요소에 'name' 또는 'artist' 가 비어있습니다.");
        }
    }

    private void checkTrackCount(int count) {
        if (count > 5 || count < 1) {
            throw new ApiException(Error.INVALID_TRACKS_COUNT_EXCEPTION, "tracks 의 요소는 1개 이상 5개 이하여야 합니다.");
        }
    }

    private void checkTitle(AlbumSaveRequest albumDto, Long accountId) {
        String title = albumDto.getTitle();

        if (!hasText(title)) {
            throw new ApiException(Error.BLANK_FIELD_EXCEPTION, "'title' 이 비어있습니다.");
        }

        if (albumRepository.existsByAccountIdAndTitle(accountId, title)) {
            throw new ApiException(Error.DUPLICATE_ALBUM_TITLE_EXCEPTION);
        }
    }

    private void checkTitle(AlbumUpdateRequest albumDto, Long accountId, Long albumId) {
        String title = albumDto.getTitle();

        if (!hasText(title)) {
            throw new ApiException(Error.BLANK_FIELD_EXCEPTION, "'title' 이 비어있습니다.");
        }

        albumRepository.findByAccountIdAndTitle(accountId, title).ifPresent(album -> {
            if (album.getId() != albumId) {
                throw new ApiException(Error.DUPLICATE_ALBUM_TITLE_EXCEPTION);
            }
        });
    }

    public void validateDeleteComment(Comment comment, SessionAccount loginAccount) {
        if (!comment.getAccountId().equals(loginAccount.getId())) {
            throw new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION);
        }
    }

    private boolean hasText(String name) {
        return StringUtils.hasText(name);
    }

}
