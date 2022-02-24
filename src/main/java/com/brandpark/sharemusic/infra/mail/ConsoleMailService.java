package com.brandpark.sharemusic.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Profile({"local", "test"})
@Service
public class ConsoleMailService implements MailService {

    @Override
    public void send(MailMessage message) {
        log.info("Send Mail : verify link = {}", message.getVerifyLink());
    }
}
