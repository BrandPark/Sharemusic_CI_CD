package com.brandpark.sharemusic.account.validator;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import com.brandpark.sharemusic.account.dto.UpdateBasicInfoForm;
import com.brandpark.sharemusic.account.dto.UpdatePasswordForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

@RequiredArgsConstructor
@Component
public class Validation {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    public void validateSignUpForm(SignUpForm form, BindingResult errors) {
        validateDuplicateEmail(form.getEmail(), errors);
        validateDuplicateNickname(form.getNickname(), errors);
        validatePasswordDiff(form.getPassword(), form.getConfirmPassword(), errors);
    }

    public void validateBasicInfoForm(Account account, UpdateBasicInfoForm form, BindingResult errors) {
        if (!form.getNickname().equals(account.getNickname())) {
            validateDuplicateNickname(form.getNickname(), errors);
        }
    }

    public void validatePasswordForm(Account account, UpdatePasswordForm form, BindingResult errors) {
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

    public void validateVerifyEmailLink(String token, String email) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(email)) {
            throw new IllegalArgumentException("유효하지 않은 링크입니다.");
        }

        Account accountByEmail = accountRepository.findByEmail(email);
        if (accountByEmail == null || !accountByEmail.getEmailCheckToken().equals(token)) {
            throw new IllegalArgumentException("토큰정보가 일치하지 않습니다.");
        }
    }
}
