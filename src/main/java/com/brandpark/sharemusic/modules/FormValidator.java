package com.brandpark.sharemusic.modules;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.form.SignUpForm;
import com.brandpark.sharemusic.modules.account.form.UpdateBasicInfoForm;
import com.brandpark.sharemusic.modules.account.form.UpdatePasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

@RequiredArgsConstructor
@Component
public class FormValidator {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public void validateSignUpForm(SignUpForm form, BindingResult errors) {
        validateDuplicateEmail(form.getEmail(), errors);
        validateDuplicateNickname(form.getNickname(), errors);
        validatePasswordDiff(form.getPassword(), form.getConfirmPassword(), errors);
    }

    public void validateBasicInfoForm(SessionAccount account, UpdateBasicInfoForm form, BindingResult errors) {
        if (!form.getNickname().equals(account.getNickname())) {
            validateDuplicateNickname(form.getNickname(), errors);
        }
    }

    public void validatePasswordForm(SessionAccount account, UpdatePasswordForm form, BindingResult errors) {
        validatePasswordDiff(form.getPassword(), form.getConfirmPassword(), errors);
        validateInputCurrentPassword(form.getCurrentPassword(), account.getPassword(), errors);
    }

    private void validateDuplicateNickname(String nickname, BindingResult errors) {
        if (accountRepository.existsByNickname(nickname)) {
            errors.rejectValue("nickname", "error.nickname", "이미 존재하는 닉네임입니다.");
        }
    }

    private void validateDuplicateEmail(String email, BindingResult errors) {
        if (accountRepository.existsByEmail(email)) {
            errors.rejectValue("email", "error.email", "이미 가입된 계정이 있습니다.");
        }
    }

    private void validateInputCurrentPassword(String inputCurrentPassword, String originCurrentPassword, BindingResult errors) {
        if (!passwordEncoder.matches(inputCurrentPassword, originCurrentPassword)) {
            errors.rejectValue("currentPassword", "error.currentPassword"
                    , "현재 비밀번호가 일치하지 않습니다.");
        }
    }

    public void validatePasswordDiff(String password, String confirmPassword, BindingResult errors) {
        if (!password.equals(confirmPassword)) {
            errors.rejectValue("confirmPassword", "error.confirmPassword", "패스워드가 일치하지 않습니다.");
        }
    }

    public void validateVerificationEmailToken(String token, String email, BindingResult errors) {

        if (!StringUtils.hasText(token) || !StringUtils.hasText(email)) {

            String message = "유효하지 않은 링크입니다. 메일 재전송 버튼을 눌러주세요.";
            errors.reject("error.verifyEmailLink", message);
        }

        Account accountByEmail = accountRepository.findByEmail(email);
        if (accountByEmail == null || !accountByEmail.getEmailCheckToken().equals(token)) {

            String message = "발신자 신원을 확인할 수 없습니다. 메일 재전송 버튼을 눌러주세요.";
            errors.reject("error.notValidToken", message);
        }

        else if (accountByEmail.getRole() == Role.USER) {

            String message = "이미 인증이 완료된 계정입니다.";
            errors.reject("error.alreadyVerified", message);
        }
    }
}
