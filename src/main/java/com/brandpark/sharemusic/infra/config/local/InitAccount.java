package com.brandpark.sharemusic.infra.config.local;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

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

        form.setEmail("user@email.com");
        form.setNickname("user");
        form.setPassword(passwordEncoder.encode("1q2w3e4r"));
        form.setName("박민곤");

        Account userAccount = modelMapper.map(form, Account.class);
        userAccount.generateEmailCheckToken();
        userAccount.assignRole(Role.USER);

        form.setEmail("guest@email.com");
        form.setNickname("guest");
        form.setPassword(passwordEncoder.encode("1q2w3e4r"));
        form.setName("박민곤");

        Account guestAccount = modelMapper.map(form, Account.class);
        guestAccount.generateEmailCheckToken();
        guestAccount.assignRole(Role.GUEST);

        accountRepository.saveAll(List.of(userAccount, guestAccount));
    }
}
