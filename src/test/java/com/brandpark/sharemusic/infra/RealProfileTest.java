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

@ActiveProfiles("real")
@SpringBootTest
class RealProfileTest {

    @Autowired MailService mailService;
    @Autowired AppProperties appProperties;

    @DisplayName("real 환경에서는 MailService 로 SendGrid 를 사용한다.")
    @Test
    public void UseSendGridMailService_When_RealProfile() throws Exception {
        assertThat(mailService instanceof SendGridMailService).isTrue();
    }

    @DisplayName("real 환경의 host는 ec2의 PUBLIC DOMAIN 이다.")
    @Test
    public void HostIsLocalhost_When_RealProfile() throws Exception {
        assertThat(appProperties.getHost()).isEqualTo("http://ec2-52-79-179-149.ap-northeast-2.compute.amazonaws.com:8080");
    }
}
