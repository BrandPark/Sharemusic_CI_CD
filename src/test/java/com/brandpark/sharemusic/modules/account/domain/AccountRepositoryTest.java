package com.brandpark.sharemusic.modules.account.domain;

import com.brandpark.sharemusic.modules.AccountFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class AccountRepositoryTest {

    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    Account account;

    @BeforeEach
    public void setUp() {
        account = accountFactory.createAccount("testAccount");
        accountRepository.save(account);
    }

    @Test
    void findByEmailOrNickname() {

        Account byNickname = accountRepository.findByEmailOrNickname(account.getNickname()).get();
        Account byEmail = accountRepository.findByEmailOrNickname(account.getEmail()).get();
        assertThat(byNickname).isNotNull();
        assertThat(byNickname == byEmail).isTrue();
    }
}