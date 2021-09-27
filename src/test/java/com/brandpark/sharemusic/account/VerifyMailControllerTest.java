package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.Role;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import com.brandpark.sharemusic.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class VerifyMailControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired EntityManager em;
    Account savedAccount;

    @BeforeEach
    public void setUp() {
        SignUpForm form = new SignUpForm();
        form.setEmail("savedAccount@email.com");
        form.setName("savedAccount");
        form.setNickname("savedAccount");
        form.setPassword("000000000");
        form.setConfirmPassword("000000000");

        savedAccount = accountService.createAccount(form);
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원가입 후 계정 검증 메일 전송안내 화면 출력")
    @Test
    public void SendEmailInfoView_After_SignUp() throws Exception {
        // given : beforeEach
        // when, then
        mockMvc.perform(get("/send-mail-info"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("accounts/mails/send-mail-info"))
                .andExpect(authenticated().withUsername("savedAccount"));
    }

    @WithUserDetails(value="savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("인증메일 검증 - 성공")
    @Test
    public void VerifyEmailLink_Success() throws Exception {
        // given : beforeEach
        // when
        mockMvc.perform(get("/verify-email")
                        .param("token", savedAccount.getEmailCheckToken())
                        .param("email", savedAccount.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/verify-email-result"));

        em.flush();
        em.clear();

        // then
        Account account = accountRepository.findByEmail(savedAccount.getEmail());

        assertThat(account.getRole()).isEqualTo(Role.USER);
    }
}