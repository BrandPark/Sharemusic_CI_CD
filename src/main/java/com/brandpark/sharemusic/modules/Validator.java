package com.brandpark.sharemusic.modules;

import com.brandpark.sharemusic.api.v1.album.dto.CreateAlbumRequest;
import com.brandpark.sharemusic.api.v1.album.dto.UpdateAlbumRequest;
import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.FollowRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.dto.CreateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdatePasswordDto;
import com.brandpark.sharemusic.modules.album.domain.AlbumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.brandpark.sharemusic.api.v1.exception.Error.*;

@RequiredArgsConstructor
@Component
public class Validator {

    private final AccountRepository accountRepository;
    private final FollowRepository followRepository;
    private final AlbumRepository albumRepository;
    private final PasswordEncoder encoder;


    public void validateUpdateAccountLogic(SessionAccount loginAccount, Long targetAccountId, UpdateAccountDto updateData) {

        checkAuthorityToModify(loginAccount, targetAccountId);

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

        checkAuthorityToModify(loginAccount, targetAccountId);

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

        checkAuthorityToModify(loginAccount, targetAccountId);

        if (loginAccount.getRole() == Role.USER) {
            throw new ApiException(ILLEGAL_ACCESS_EXCEPTION
                    , "'" + loginAccount.getEmail() + "' 은 이미 인증된 이메일 계정입니다.");

        } else if (!StringUtils.hasText(emailCheckToken) || !loginAccount.getEmailCheckToken().equals(emailCheckToken)) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "인증 토큰이 유효하지 않습니다.");
        }
    }

    public void validateFindAllFollowers(Long targetAccountId) {
        checkExistAccountById(targetAccountId);
    }

    public void validateFindAllFollowings(Long targetAccountId) {
        checkExistAccountById(targetAccountId);
    }

    public void validateFollow(SessionAccount loginAccount, Long targetAccountId) {
        checkExistAccountById(targetAccountId);

        checkEnableFollow(loginAccount.getId(), targetAccountId);
    }

    public void validateUnfollow(SessionAccount loginAccount, Long targetAccountId) {
        checkExistAccountById(targetAccountId);

        checkEnableUnfollow(loginAccount.getId(), targetAccountId);
    }

    public void validateCreateAlbum(SessionAccount loginAccount, CreateAlbumRequest reqDto) {
        checkDuplicatedAlbumTitle(loginAccount.getId(), reqDto.getTitle(), null);

        distinctTrackList(reqDto.getTracks());
    }

    public void validateUpdateAlbum(SessionAccount loginAccount, UpdateAlbumRequest reqDto, Long albumId) {
        checkExistAlbumById(albumId);

        checkDuplicatedAlbumTitle(loginAccount.getId(), reqDto.getTitle(), albumId);
    }

    private void checkExistAlbumById(Long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new ApiException(ILLEGAL_ARGUMENT_EXCEPTION, "존재하지 않는 앨범입니다.");
        }
    }

    private <T> void distinctTrackList(List<T> tracks) {
        List<T> distinctTrackList = tracks.stream().distinct().collect(Collectors.toList());

        if (tracks.size() > distinctTrackList.size()) {
            tracks.clear();
            tracks.addAll(distinctTrackList);
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

    private void checkExistAccountById(Long targetAccountId) {
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

    private void checkAuthorityToModify(SessionAccount loginAccount, Long targetAccountId) {
        if (!loginAccount.getId().equals(targetAccountId)) {
            throw new ApiException(FORBIDDEN_EXCEPTION);
        }
    }
}
