package com.brandpark.sharemusic.modules.account.service;

import com.brandpark.sharemusic.infra.config.AppProperties;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class VerifyMailService {

    private final MailService mailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    @Transactional
    public void sendSignUpConfirmMail(SessionAccount account) {

        Context context = new Context();
        context.setVariable("nickname", account.getNickname());
        context.setVariable("message", "이메일 인증을 위해서 아래의 버튼을 클릭해주세요.");
        context.setVariable("link", "/verify-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("linkText", account.getEmail() + " 인증하기");

        String html = templateEngine.process("/accounts/mails/verification-email", context);

        MailMessage message = MailMessage.builder()
                .to(account.getEmail())
                .subject("ShareMusic 회원가입 이메일 인증")
                .text(html)
                .build();

        mailService.send(message);
    }
}
