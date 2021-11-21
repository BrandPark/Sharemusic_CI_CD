package com.brandpark.sharemusic.api.v1;

import com.brandpark.sharemusic.api.v1.account.dto.CreateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdateAccountRequest;
import com.brandpark.sharemusic.api.v1.account.dto.UpdatePasswordRequest;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.AlbumUpdateRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackSaveRequest;
import com.brandpark.sharemusic.api.v1.album.dto.TrackUpdateRequest;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import com.brandpark.sharemusic.modules.comment.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.brandpark.sharemusic.api.v1.exception.Error.*;

@RequiredArgsConstructor
@Component
public class DtoValidator {

    private final AlbumRepository albumRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

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

    public void validateNotification(Long loginId, Long notificationAccountId) {
        if (!loginId.equals(notificationAccountId)) {
            throw new ApiException(Error.FORBIDDEN_EXCEPTION, "해당 알림에 대한 권한이 없습니다.");
        }
    }

    public void validateUpdateAccountData(UpdateAccountRequest reqDto, SessionAccount loginAccount, Long targetAccountId) {

        final String name = reqDto.getName();
        final String nickname = reqDto.getNickname();

        if (!nickname.equals(loginAccount.getNickname())) {
            validateDuplicateNickname(nickname);
        }
    }

    public void validateCreateAccountData(CreateAccountRequest reqDto) {
        final String nickname = reqDto.getNickname();
        final String password = reqDto.getPassword();
        final String confirmPassword = reqDto.getConfirmPassword();

        validateDuplicateNickname(nickname);
        validatePassword(password, confirmPassword);
    }

    public void validateSameAccount(SessionAccount loginAccount, Long targetAccountId) {
        if (!loginAccount.getId().equals(targetAccountId)) {
            throw new ApiException(FORBIDDEN_EXCEPTION);
        }
    }

    public void validateEmailCheckToken(Long targetAccountId, String emailCheckToken) {

        Account myAccount = accountRepository.findById(targetAccountId).get();

        if (myAccount.getRole() == Role.USER) {
            throw new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION
                    , "'" + myAccount.getEmail() + "' 은 이미 인증된 이메일 계정입니다.");
        } else if (!myAccount.getEmailCheckToken().equals(emailCheckToken)) {
            throw new ApiException(Error.ILLEGAL_ARGUMENT_EXCEPTION, "인증 토큰이 일치하지 않습니다.");
        }
    }

    private void validatePassword(String password, String confirmPassword) {
        if (!StringUtils.hasText(password)) {
            throw new ApiException(Error.ILLEGAL_ARGUMENT_EXCEPTION, "비밀번호를 입력해주세요");
        } else {
            if (!StringUtils.hasText(confirmPassword) || !confirmPassword.equals(password)) {
                throw new ApiException(Error.ILLEGAL_ARGUMENT_EXCEPTION, "비밀번호가 일치하지 않습니다.");
            }
        }
    }

    private void validateDuplicateNickname(String nickname) {
        if (accountRepository.existsByNickname(nickname)) {
            throw new ApiException(DUPLICATE_FIELD_EXCEPTION, "이미 존재하는 닉네임입니다.");
        }
    }

    private boolean hasText(String name) {
        return StringUtils.hasText(name);
    }

    public void validateUpdatePassword(UpdatePasswordRequest reqDto, Long targetAccountId) {

        final Account myAccount = accountRepository.findById(targetAccountId).get();
        final String originPassword = reqDto.getOriginPassword();
        final String updatePassword = reqDto.getUpdatePassword();
        final String confirmPassword = reqDto.getConfirmPassword();

        if (!StringUtils.hasText(originPassword)) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "현재 비밀번호를 입력 해주세요");
        }
        if (!passwordEncoder.matches(originPassword, myAccount.getPassword())) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "현재 비밀번호가 일치하지 않습니다.");
        }

        validatePassword(updatePassword, confirmPassword);

        if (originPassword.equals(updatePassword)) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "변경할 비밀번호가 현재 비밀번호와 같습니다.");
        }

    }
}
