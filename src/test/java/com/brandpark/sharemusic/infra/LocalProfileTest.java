package com.brandpark.sharemusic.infra;

import com.brandpark.sharemusic.infra.config.AppProperties;
import com.brandpark.sharemusic.infra.mail.ConsoleMailService;
import com.brandpark.sharemusic.infra.mail.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest
public class LocalProfileTest {

    @Autowired MailService mailService;
    @Autowired AppProperties appProperties;

    @DisplayName("local 환경에서는 MailService 로 ConsoleGrid 를 사용한다.")
    @Test
    public void UseConsoleMailService_When_LocalProfile() throws Exception {
        assertThat(mailService instanceof ConsoleMailService).isTrue();
    }

    @DisplayName("local 환경의 host 는 localhost:8080이다.")
    @Test
    public void HostIsLocalhost_When_LocalProfile() throws Exception {
        assertThat(appProperties.getHost()).isEqualTo("http://localhost:8080");
    }
}
