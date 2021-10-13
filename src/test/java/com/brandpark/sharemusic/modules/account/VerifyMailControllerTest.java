package com.brandpark.sharemusic.modules.account;

import com.brandpark.sharemusic.infra.MockMvcTest;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class VerifyMailControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired EntityManager em;
    @Autowired AccountFactory accountFactory;
    @MockBean MailService mailService;
    Account guestAccount;
    Account verifiedAccount;

    @BeforeEach
    public void setUp() {
        guestAccount = accountFactory.createAccount("guestAccount");
        verifiedAccount = accountFactory.createAccount("verifiedAccount", Role.USER);
        accountRepository.saveAll(List.of(guestAccount, verifiedAccount));
    }

    @WithUserDetails(value = "verifiedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("인증 메일 재전송 - 실패(이미 인증된 계정)")
    @Test
    public void ResendVerifyMail_Fail_When_AlreadyVerified() throws Exception {

        // given : beforeEach
        // when, then
        mockMvc.perform(post("/resend-verify-mail")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("인증 메일 재전송 - 성공")
    @Test
    public void ResendVerifyMail_Success() throws Exception {
        // given : beforeEach
        // when, then
        mockMvc.perform(post("/resend-verify-mail")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/send-mail-info"));

        then(mailService).should().send(any(MailMessage.class));
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원가입 후 계정 검증 메일 전송안내 화면 출력")
    @Test
    public void SendEmailInfoView_After_SignUp() throws Exception {
        // given : beforeEach
        // when, then
        mockMvc.perform(get("/send-mail-info"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("accounts/mails/send-mail-info"))
                .andExpect(authenticated().withUsername("guestAccount"));
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 인증 - 실패(유효하지 않은 토큰)")
    @Test
    public void VerifyEmailLink_Fail_When_InputNotValidToken() throws Exception {
        // given : beforeEach
        String notValidToken = guestAccount.getEmailCheckToken() + "diff";

        // when
        mockMvc.perform(get("/verify-email")
                        .param("token", notValidToken)
                        .param("email", guestAccount.getEmail())
                )
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account", "verificationEmailToken"))
                .andExpect(view().name("accounts/mails/verify-email-result"));
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 인증 - 실패(이미 인증이 완료된 계정)")
    @Test
    public void VerifyEmailLink_Fail_When_AlreadyVerifiedEmail() throws Exception {
        // given : beforeEach
        guestAccount.assignRole(Role.USER);

        // when
        mockMvc.perform(get("/verify-email")
                        .param("token", guestAccount.getEmailCheckToken())
                        .param("email", guestAccount.getEmail())
                )
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account", "verificationEmailToken"))
                .andExpect(view().name("accounts/mails/verify-email-result"));
    }

    @WithUserDetails(value = "guestAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("계정 인증 - 성공")
    @Test
    public void VerifyEmailLink_Success() throws Exception {
        // given : beforeEach
        // when
        mockMvc.perform(get("/verify-email")
                        .param("token", guestAccount.getEmailCheckToken())
                        .param("email", guestAccount.getEmail())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(view().name("redirect:/verify-email-result"));

        em.flush();
        em.clear();

        // then
        Account account = accountRepository.findByEmail(guestAccount.getEmail());

        assertThat(account.getRole()).isEqualTo(Role.USER);
    }
}