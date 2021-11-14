package com.brandpark.sharemusic.modules.account;

import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@MockMvcTest
public class RememberMeTest {

    @MockBean PersistentTokenRepository tokenRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired MockMvc mockMvc;
    Account savedAccount;

    @BeforeEach
    public void setUp() {
        savedAccount = accountFactory.createAccount("savedAccount");
        accountRepository.save(savedAccount);
    }

    @DisplayName("로그인에 성공하면 토큰을 생성한다.")
    @Test
    public void CreateToken_When_LoginSuccess() throws Exception {
        // given : beforeEach
        // when, then
        mockMvc.perform(post("/login")
                .param("username", savedAccount.getEmail())
                .param("password", "000000000")
                .param("remember-me", "on")
                .with(csrf()));

        then(tokenRepository).should().createNewToken(any(PersistentRememberMeToken.class));
    }
}
