package com.brandpark.sharemusic.modules.main;

import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Profile("dev")
@Component
public class DevInitData {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Account guestAccount = Account.createAccount(
                "alsrhs0530@gmail.com",
                "박민곤",
                "guest",
                passwordEncoder.encode("1q2w3e4r")
        );

        Account userAccount = Account.createAccount(
                "pak2005@hanmail.net",
                "박민곤",
                "user",
                passwordEncoder.encode("1q2w3e4r")
        );
        userAccount.assignRole(Role.USER);

        accountRepository.saveAll(List.of(guestAccount, userAccount));
    }
}
