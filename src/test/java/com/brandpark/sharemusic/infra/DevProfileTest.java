package com.brandpark.sharemusic.infra;

import com.brandpark.sharemusic.infra.config.AppProperties;
import com.brandpark.sharemusic.infra.mail.MailService;
import com.brandpark.sharemusic.infra.mail.SendGridMailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest
class DevProfileTest {

    @Autowired MailService mailService;
    @Autowired AppProperties appProperties;

    @DisplayName("dev 환경에서는 MailService 로 SendGrid 를 사용한다.")
    @Test
    public void UseSendGridMailService_When_DevProfile() throws Exception {
        assertThat(mailService instanceof SendGridMailService).isTrue();
    }

    @DisplayName("dev 환경의 host는 localhost:8080이다.")
    @Test
    public void HostIsLocalhost_When_DevProfile() throws Exception {
        assertThat(appProperties.getHost()).isEqualTo("http://localhost:8080");
    }
}
