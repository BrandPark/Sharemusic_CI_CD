package com.brandpark.sharemusic.account.validator;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.dto.EmailCheckToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class EmailCheckTokenValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return EmailCheckToken.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EmailCheckToken emailCheckToken = (EmailCheckToken) target;
        String token = emailCheckToken.getToken();
        String email = emailCheckToken.getEmail();

        if (!StringUtils.hasText(token) || !StringUtils.hasText(email)) {
            throw new IllegalArgumentException("유효하지 않은 링크입니다.");
        }

        Account account = accountRepository.findByEmail(email);
        if (account == null || !account.getEmailCheckToken().equals(token)) {
            throw new IllegalArgumentException("유효하지 않은 링크입니다.");
        }
    }
}
