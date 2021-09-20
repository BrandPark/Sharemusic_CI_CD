package com.brandpark.sharemusic.infra.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsoleMailService implements MailService {

    @Override
    public void send(MailMessage message) {
        log.info("Send Mail : Text = {}", message.getText());
        throw new RuntimeException("메시지 에러");
    }
}
