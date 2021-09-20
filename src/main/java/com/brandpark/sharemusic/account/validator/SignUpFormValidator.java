package com.brandpark.sharemusic.account.validator;

import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return SignUpForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        SignUpForm form = (SignUpForm) target;
        if (accountRepository.existsByEmail(form.getEmail())) {
            errors.rejectValue("email", "error.email", "이미 가입된 계정이 있습니다.");
        }
        if (accountRepository.existsByNickname(form.getNickname())) {
            errors.rejectValue("nickname", "error.nickname", "이미 존재하는 닉네임입니다.");
        }
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "error.confirmPassword", "패스워드가 일치하지 않습니다.");
        }
    }
}
