package com.brandpark.sharemusic.api.v1;

import com.brandpark.sharemusic.api.v1.album.dto.CreateAlbumRequest;
import com.brandpark.sharemusic.api.v1.album.dto.UpdateAlbumRequest;
import com.brandpark.sharemusic.api.v1.album.dto.UpdateAlbumRequest.UpdateTrackRequest;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.account.dto.CreateAccountDto;
import com.brandpark.sharemusic.modules.account.account.dto.UpdateAccountDto;
import com.brandpark.sharemusic.modules.account.account.dto.UpdatePasswordDto;
import com.brandpark.sharemusic.modules.account.follow.domain.FollowRepository;
import com.brandpark.sharemusic.modules.album.domain.*;
import com.brandpark.sharemusic.modules.notification.domain.Notification;
import com.brandpark.sharemusic.modules.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.brandpark.sharemusic.api.v1.exception.Error.*;

@RequiredArgsConstructor
@Component
public class Validator {

    private final AccountRepository accountRepository;
    private final FollowRepository followRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final PasswordEncoder encoder;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;

    public void validateUpdateAccountLogic(SessionAccount loginAccount, Long targetAccountId, UpdateAccountDto updateData) {

        checkAuthorityToUpdate(loginAccount, targetAccountId);

        final String currentNickname = loginAccount.getNickname();
        final String updateNickname = updateData.getNickname();
        final boolean isModifiedNickname = !updateNickname.equals(currentNickname);

        if (isModifiedNickname) {
            checkDuplicatedNickname(updateNickname);
        }
    }

    public void validateCreateAccountLogic(CreateAccountDto createData) {
        final String email = createData.getEmail();
        final String nickname = createData.getNickname();
        final String password = createData.getPassword();
        final String confirmPassword = createData.getConfirmPassword();

        checkDuplicatedEmail(email);
        checkDuplicatedNickname(nickname);
        checkCorrectConfirmPassword(password, confirmPassword);
    }

    private void checkDuplicatedEmail(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new ApiException(DUPLICATE_FIELD_EXCEPTION, "이미 계정이 존재하는 이메일입니다.");
        }
    }

    public void validateUpdatePasswordLogic(SessionAccount loginAccount, Long targetAccountId, UpdatePasswordDto updateData) {

        checkAuthorityToUpdate(loginAccount, targetAccountId);

        final String originPassword = updateData.getOriginPassword();
        final String updatePassword = updateData.getUpdatePassword();
        final String confirmPassword = updateData.getConfirmPassword();

        if (!encoder.matches(originPassword, loginAccount.getPassword())) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "현재 비밀번호가 일치하지 않습니다.");
        }

        checkCorrectConfirmPassword(updatePassword, confirmPassword);

        if (originPassword.equals(updatePassword)) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "변경할 비밀번호가 현재 비밀번호와 같습니다.");
        }
    }

    public void validateVerifyEmailCheckTokenLogic(SessionAccount loginAccount, Long targetAccountId, String emailCheckToken) {

        checkAuthorityToUpdate(loginAccount, targetAccountId);

        if (loginAccount.getRole() == Role.USER) {
            throw new ApiException(ILLEGAL_ACCESS_EXCEPTION
                    , "'" + loginAccount.getEmail() + "' 은 이미 인증된 이메일 계정입니다.");

        } else if (!StringUtils.hasText(emailCheckToken) || !loginAccount.getEmailCheckToken().equals(emailCheckToken)) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "인증 토큰이 유효하지 않습니다.");
        }
    }

    public void validateFindAllFollowers(Long targetAccountId) {
        checkExistsAccountById(targetAccountId);
    }

    public void validateFindAllFollowings(Long targetAccountId) {
        checkExistsAccountById(targetAccountId);
    }

    public void validateFollow(SessionAccount loginAccount, Long targetAccountId) {
        checkExistsAccountById(targetAccountId);

        checkEnableFollow(loginAccount.getId(), targetAccountId);
    }

    public void validateUnfollow(SessionAccount loginAccount, Long targetAccountId) {
        checkExistsAccountById(targetAccountId);

        checkEnableUnfollow(loginAccount.getId(), targetAccountId);
    }

    public void validateCreateAlbum(SessionAccount loginAccount, CreateAlbumRequest reqDto) {
        checkDuplicatedAlbumTitle(loginAccount.getId(), reqDto.getTitle(), null);

        checkDuplicateTrack(reqDto.getTracks());
    }

    public void validateUpdateAlbum(SessionAccount loginAccount, UpdateAlbumRequest reqDto, Long albumId) {
        checkExistsAlbumById(albumId);

        checkAuthorityToUpdateAlbum(loginAccount, albumId);

        checkDuplicatedAlbumTitle(loginAccount.getId(), reqDto.getTitle(), albumId);

        checkTrackCountConsiderTrackStatus(reqDto);

        checkTrackStatus(reqDto, albumId);

        exceptDuplicateTrackConsiderTrackStatus(reqDto);
    }

    public void validateDeleteAlbum(SessionAccount loginAccount, Long albumId) {
        checkExistsAlbumById(albumId);

        checkAuthorityToUpdateAlbum(loginAccount, albumId);
    }

    public void validateFindAllComments(Long albumId) {
        checkExistsAlbumById(albumId);
    }

    public void validateCreateComment(Long albumId, String content) {
        checkExistsAlbumById(albumId);

        if (!StringUtils.hasText(content)) {
            throw new ApiException(Error.BLANK_FIELD_EXCEPTION, "댓글 내용을 입력 해주세요.");
        }
    }

    public void validateDeleteComment(SessionAccount loginAccount, Long albumId, Long commentId) {
        checkExistsAlbumById(albumId);

        checkCommentInAlbum(albumId, commentId);

        checkAuthorityToUpdateComment(loginAccount, commentId);
    }

    public void validateReadCheckNotification(SessionAccount account, Long notificationId) {
        checkExistsNotification(notificationId);

        checkAuthorityToReadCheckNotification(account, notificationId);
    }

    private void checkAuthorityToReadCheckNotification(SessionAccount account, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).get();
        checkAuthorityToUpdate(account, notification.getAccountId());
    }

    private void checkExistsNotification(Long notificationId) {
        boolean exists = notificationRepository.existsById(notificationId);
        if (!exists) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "해당 알림이 존재하지 않습니다.");
        }
    }

    private void checkAuthorityToUpdateComment(SessionAccount loginAccount, Long commentId) {
        Comment comment = commentRepository.findById(commentId).get();

        checkAuthorityToUpdate(loginAccount, comment.getAccountId());
    }

    private void checkCommentInAlbum(Long albumId, Long commentId) {
        boolean existsInAlbum = commentRepository.existsByIdAndAlbumId(commentId, albumId);
        if (!existsInAlbum) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "앨범에 해당 댓글이 존재하지 않습니다.");
        }
    }

    private void checkAuthorityToUpdateAlbum(SessionAccount loginAccount, Long albumId) {
        Album album = albumRepository.findById(albumId).get();
        checkAuthorityToUpdate(loginAccount, album.getAccountId());
    }

    private void exceptDuplicateTrackConsiderTrackStatus(UpdateAlbumRequest reqDto) {
        List<UpdateTrackRequest> notRemoveTracks = reqDto.getTracks().stream()
                .filter(t -> t.getStatus() != TrackStatus.REMOVE)
                .collect(Collectors.toList());

        List<UpdateTrackRequest> removeTracks = reqDto.getTracks().stream()
                .filter(t -> t.getStatus() == TrackStatus.REMOVE)
                .collect(Collectors.toList());

        checkDuplicateTrack(notRemoveTracks);

        List<UpdateTrackRequest> exceptDuplicateResult = Stream.concat(notRemoveTracks.stream(), removeTracks.stream())
                .collect(Collectors.toList());

        reqDto.setTracks(exceptDuplicateResult);
    }

    private void checkTrackStatus(UpdateAlbumRequest reqDto, Long albumId) {
        for (UpdateTrackRequest track : reqDto.getTracks()) {

            Long trackId = track.getId();

            switch (track.getStatus()) {
                case INSERT:
                    if (trackId != null) {
                        throw new ApiException(ILLEGAL_ACCESS_EXCEPTION, "INSERT 상태의 TRACK 은 ID가 없어야 합니다.");
                    }
                    break;
                case UPDATE:
                case REMOVE:
                    if (trackId == null) {
                        throw new ApiException(ILLEGAL_ACCESS_EXCEPTION, track.getStatus().name() + " 상태의 TRACK 은 ID가 있어야 합니다.");
                    } else {
                        boolean existsTrack = trackRepository.existsTrackByIdAndAlbumId(trackId, albumId);
                        if (!existsTrack) {
                            throw new ApiException(ILLEGAL_ACCESS_EXCEPTION, track.getStatus().name() + " 는 앨범에 트랙이 존재해야만 합니다.");
                        }
                    }
                    break;
            }
        }
    }

    private void checkTrackCountConsiderTrackStatus(UpdateAlbumRequest reqDto) {

        int trackCount = reqDto.getTracks().size();

        for (UpdateTrackRequest trackDto : reqDto.getTracks()) {
            if (trackDto.getStatus() == TrackStatus.REMOVE) {
                trackCount--;
            }
        }

        if (trackCount == 0) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "트랙을 한 개 이상 넣어주세요.");
        } else if (trackCount > 5) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "트랙의 수는 최대 5개입니다.");
        }
    }

    private void checkExistsAlbumById(Long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "존재하지 않는 앨범입니다.");
        }
    }

    private <T> void checkDuplicateTrack(List<T> tracks) {
        List<T> distinctTrackList = tracks.stream().distinct().collect(Collectors.toList());

        if (tracks.size() > distinctTrackList.size()) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "저장될 트랙 중 중복된 트랙이 존재합니다.");
        }
    }

    private void checkDuplicatedAlbumTitle(Long albumAccountId, String title, Long albumId) {
        if (albumId == null) {
            if (albumRepository.existsByAccountIdAndTitle(albumAccountId, title)) {
                throw new ApiException(DUPLICATE_FIELD_EXCEPTION, "유저가 같은 제목의 앨범을 이미 갖고 있습니다.");
            }
        } else {
            albumRepository.findByAccountIdAndTitle(albumAccountId, title)
                    .ifPresent(duplicatedTitleAlbum -> {
                        boolean isSameAlbum = duplicatedTitleAlbum.getId() == albumId;

                        if (isSameAlbum) {
                            return;
                        }

                        throw new ApiException(DUPLICATE_FIELD_EXCEPTION, "유저가 같은 제목의 앨범을 이미 갖고 있습니다.");
                    });
        }
    }

    private void checkEnableFollow(Long loginAccountId, Long targetAccountId) {
        if (followRepository.isFollowing(loginAccountId, targetAccountId)) {
            throw new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION, "이미 팔로우 중입니다.");
        }
        if (loginAccountId.equals(targetAccountId)) {
            throw new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION, "자기 자신은 팔로우 할 수 없습니다.");
        }
    }

    private void checkEnableUnfollow(Long loginAccountId, Long targetAccountId) {
        if (!followRepository.isFollowing(loginAccountId, targetAccountId)) {
            throw new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION, "팔로잉 상태가 아닙니다.");
        }
    }

    private void checkExistsAccountById(Long targetAccountId) {
        if (!accountRepository.existsById(targetAccountId)) {
            throw new ApiException(NOT_FOUND_ACCOUNT_EXCEPTION);
        }
    }

    private void checkCorrectConfirmPassword(String password, String confirmPassword) {
        if (!confirmPassword.equals(password)) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "비밀번호가 일치하지 않습니다.");
        }
    }

    private void checkDuplicatedNickname(String updateNickname) {
        if (accountRepository.existsByNickname(updateNickname)) {
            throw new ApiException(DUPLICATE_FIELD_EXCEPTION, "이미 존재하는 닉네임입니다.");
        }
    }

    private void checkAuthorityToUpdate(SessionAccount loginAccount, Long targetAccountId) {
        if (!loginAccount.getId().equals(targetAccountId)) {
            throw new ApiException(FORBIDDEN_EXCEPTION);
        }
    }
}
