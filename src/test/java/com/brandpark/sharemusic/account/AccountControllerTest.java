package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.Role;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class AccountControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired EntityManager em;
    @MockBean MailService mailService;
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

    @DisplayName("회원가입 화면출력")
    @Test
    public void SignUpForm() throws Exception {
        mockMvc.perform(get("/accounts/signup"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(view().name("accounts/signup"));
    }

    @DisplayName("회원가입 처리 - 입력 값 오류 (email 형식)")
    @Test
    public void SignUpSubmit_Fail_When_InputNotEmailFormat() throws Exception {
        // given
        SignUpForm form = createForm();
        form.setEmail("wrong-email-format");

        // when, then
        mockMvc.perform(post("/accounts/signup")
                        .param("email", form.getEmail())
                        .param("name", form.getName())
                        .param("nickname", form.getNickname())
                        .param("password", form.getPassword())
                        .param("confirmPassword", form.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("signUpForm", "email", "Email"))
                .andExpect(view().name("accounts/signup"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 처리 - 입력 값 오류 (password 불일치)")
    @Test
    public void SignUpSubmit_Fail_When_InputDifferentPassword() throws Exception {
        // given
        SignUpForm form = createForm();
        form.setPassword("123123123");
        form.setConfirmPassword("456456456");

        // when, then
        mockMvc.perform(post("/accounts/signup")
                        .param("email", form.getEmail())
                        .param("name", form.getName())
                        .param("nickname", form.getNickname())
                        .param("password", form.getPassword())
                        .param("confirmPassword", form.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("signUpForm", "confirmPassword", "error.confirmPassword"))
                .andExpect(view().name("accounts/signup"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 처리 - 입력 값 오류 (email 중복)")
    @Test
    public void SignUpSubmit_Fail_When_InputDuplicateEmail() throws Exception {
        // given
        SignUpForm form = createForm();
        form.setEmail(savedAccount.getEmail());
        form.setNickname(savedAccount.getNickname() + "diff");

        // when, then
        mockMvc.perform(post("/accounts/signup")
                        .param("email", form.getEmail())
                        .param("name", form.getName())
                        .param("nickname", form.getNickname())
                        .param("password", form.getPassword())
                        .param("confirmPassword", form.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("signUpForm", "email", "error.email"))
                .andExpect(view().name("accounts/signup"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 처리 - 입력 값 오류 (nickname 중복)")
    @Test
    public void SignUpSubmit_Fail_When_InputDuplicateNickname() throws Exception {
        // given
        SignUpForm form = createForm();
        form.setEmail(savedAccount.getEmail() + "diff");
        form.setNickname(savedAccount.getNickname());

        // when, then
        mockMvc.perform(post("/accounts/signup")
                        .param("email", form.getEmail())
                        .param("name", form.getName())
                        .param("nickname", form.getNickname())
                        .param("password", form.getPassword())
                        .param("confirmPassword", form.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("signUpForm", "nickname", "error.nickname"))
                .andExpect(view().name("accounts/signup"))
                .andExpect(unauthenticated());
    }

    @DisplayName("회원가입 처리 - 성공")
    @Test
    public void SignUpSubmit_Success() throws Exception {
        // given
        SignUpForm form = createForm();

        // when, then
        mockMvc.perform(post("/accounts/signup")
                        .param("email", form.getEmail())
                        .param("name", form.getName())
                        .param("nickname", form.getNickname())
                        .param("password", form.getPassword())
                        .param("confirmPassword", form.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/accounts/sendmail"))
                .andExpect(authenticated().withUsername(form.getNickname()));

        Account account = accountRepository.findByEmail(form.getEmail());
        assertThat(account).isNotNull();
        assertThat(account.getEmailCheckToken()).isNotNull();
        assertThat(account.getEmailCheckTokenGeneratedAt()).isNotNull();
        assertThat(account.getRole()).isEqualTo(Role.GUEST);
        assertTrue(passwordEncoder.matches(form.getPassword(), account.getPassword()));
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("회원 가입 후 처리 - 본인인증 메일 전송")
    @Test
    public void SendEmail_After_SignUp() throws Exception {
        // given : beforeEach

        // when, then
        mockMvc.perform(get("/accounts/sendmail"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("accounts/email-check-info"))
                .andExpect(authenticated().withUsername("savedAccount"));

        then(mailService).should().send(any(MailMessage.class));
    }

    @WithUserDetails(value="savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("인증메일 검증 - 성공")
    @Test
    public void VerifyEmailLink_Success() throws Exception {
        // given : beforeEach
        // when
        mockMvc.perform(get("/accounts/check-email-token")
                        .param("token", savedAccount.getEmailCheckToken())
                        .param("email", savedAccount.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));

        em.flush();
        em.clear();

        // then
        Account account = accountRepository.findByEmail(savedAccount.getEmail());

        assertThat(account.getRole()).isEqualTo(Role.USER);
    }

    private SignUpForm createForm() {

        SignUpForm form = new SignUpForm();

        form.setName("newAccount");
        form.setNickname("newAccount");
        form.setPassword("111111111");
        form.setConfirmPassword("111111111");
        form.setEmail("newAccount@email.com");

        return form;
    }
}