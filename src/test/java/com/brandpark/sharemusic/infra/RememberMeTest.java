package com.brandpark.sharemusic.infra;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import com.brandpark.sharemusic.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("local")
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class RememberMeTest {

    @MockBean PersistentTokenRepository tokenRepository;
    @Autowired AccountService accountService;
    @Autowired MockMvc mockMvc;
    Account savedAccount;

    @BeforeEach
    public void setUp() {
        SignUpForm form = new SignUpForm();
        form.setEmail("savedAccount@email.com");
        form.setName("savedAccount");
        form.setNickname("savedAccount");
        form.setPassword("000000000");
        form.setConfirmPassword("000000000");

        savedAccount = accountService.signUp(form);
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
