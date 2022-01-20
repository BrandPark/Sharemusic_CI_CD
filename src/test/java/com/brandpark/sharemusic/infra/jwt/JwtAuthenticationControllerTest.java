package com.brandpark.sharemusic.infra.jwt;

import com.brandpark.sharemusic.MockMvcTest;
import com.brandpark.sharemusic.infra.jwt.dto.JwtRequest;
import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class JwtAuthenticationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountFactory accountFactory;
    @Autowired ObjectMapper objectMapper;
    Account user;
    String pw;

    @BeforeEach
    public void setUp() {
        user = accountFactory.persistAccount("user");
        pw = accountFactory.getPassword();
    }

    @DisplayName("인증 후 jwt 토큰을 반환받는다.")
    @Test
    public void AuthenticateAndJwtToken() throws Exception {

        JwtRequest req = new JwtRequest(user.getNickname(), pw);

        // given
        mockMvc.perform(post("/authenticate")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8.name())
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        // when

        // then
    }

}