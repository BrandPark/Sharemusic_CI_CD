package com.brandpark.sharemusic.account.service;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.Role;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VerifyMailService {

    private final MailService mailService;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Transactional
    public void sendConfirmMail(Account account) {

        MailMessage message = new MailMessage();
        message.setText("/verify-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        message.setTitle("ShareMusic");
        message.setTo(account.getEmail());

        mailService.send(message);
    }

    @Transactional
    public void succeedVerifyEmailCheckToken(Account account) {
        account.assignRole(Role.USER);
        accountRepository.save(account);

        accountService.login(account);
    }

}
