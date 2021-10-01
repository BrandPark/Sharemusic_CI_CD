package com.brandpark.sharemusic.infra.config;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.dto.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Profile("local")
@Component
public class InitAccount {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;

    @PostConstruct
    public void initAccount() {
        SignUpForm form = new SignUpForm();
        form.setEmail("test@email.com");
        form.setNickname("test");
        form.setPassword(passwordEncoder.encode("123123123"));
        form.setName("박민곤");

        Account account = modelMapper.map(form, Account.class);
        account.generateEmailCheckToken();
        account.assignRole(Role.GUEST);

        accountRepository.save(account);
    }
}
