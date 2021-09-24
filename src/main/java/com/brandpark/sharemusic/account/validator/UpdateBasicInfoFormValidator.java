package com.brandpark.sharemusic.account.validator;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.dto.UpdateBasicInfoForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class UpdateBasicInfoFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateBasicInfoForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UpdateBasicInfoForm form = (UpdateBasicInfoForm) target;

        Account account = accountRepository.findByNickname(form.getNickname());

        if (account != null && !account.getEmail().equals(form.getEmail())) {
            errors.rejectValue("nickname", "error.nickname", "이미 존재하는 닉네임입니다.");
        }
    }
}
