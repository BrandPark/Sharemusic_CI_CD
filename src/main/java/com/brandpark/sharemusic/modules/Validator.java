package com.brandpark.sharemusic.modules;

import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.dto.CreateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdatePasswordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.brandpark.sharemusic.api.v1.exception.Error.*;

@RequiredArgsConstructor
@Component
public class Validator {

    private final AccountRepository accountRepository;
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
