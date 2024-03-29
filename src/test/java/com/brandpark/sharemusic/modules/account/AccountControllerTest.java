package com.brandpark.sharemusic.modules.account;

import com.brandpark.sharemusic.MockMvcTest;
import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.account.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.account.form.SignUpForm;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

@MockMvcTest
class AccountControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired AccountFactory accountFactory;
    @MockBean MailService mailService;
    Account savedAccount;
    Account loginAccount;
    Account otherAccount;

    @BeforeEach
    public void setUp() {
        savedAccount = accountFactory.createAccount("savedAccount");
        loginAccount = accountFactory.createAccount("loginAccount");
        otherAccount = accountFactory.createAccount("otherAccount");

        accountRepository.saveAll(List.of(savedAccount, loginAccount, otherAccount));
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
        SignUpForm form = accountFactory.createSignUpForm("newAccount");
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
        SignUpForm form = accountFactory.createSignUpForm("newAccount");
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
        SignUpForm form = accountFactory.createSignUpForm("newAccount");
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
        SignUpForm form = accountFactory.createSignUpForm("");
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
        SignUpForm form = accountFactory.createSignUpForm("newAccount");

        // when, then
        mockMvc.perform(post("/accounts/signup")
                        .param("email", form.getEmail())
                        .param("name", form.getName())
                        .param("nickname", form.getNickname())
                        .param("password", form.getPassword())
                        .param("confirmPassword", form.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/send-mail-info"))
                .andExpect(authenticated().withUsername(form.getNickname()));

        then(mailService).should().send(any(MailMessage.class));

        Account account = accountRepository.findByEmail(form.getEmail());
        assertThat(account).isNotNull();
        assertThat(account.getEmailCheckToken()).isNotNull();
        assertThat(account.getEmailCheckTokenGeneratedAt()).isNotNull();
        assertThat(account.getRole()).isEqualTo(Role.GUEST);
        assertTrue(passwordEncoder.matches(form.getPassword(), account.getPassword()));
    }

    @WithUserDetails(value = "loginAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("다른 사람 프로필 화면 출력")
    @Test
    public void ProfileView_When_VisitOtherAccountProfile() throws Exception {

        String otherAccountNickname = otherAccount.getNickname();
        mockMvc.perform(get("/accounts/" + otherAccountNickname))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("targetAccount", "account", "isFollowing", "isOwner", "friendshipData"))
                .andExpect(view().name("accounts/profile"));
    }

    @WithUserDetails(value = "loginAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("자신의 프로필 화면 출력")
    @Test
    public void ProfileView_When_VisitMyAccountProfile() throws Exception {
        String myAccountNickname = loginAccount.getNickname();
        mockMvc.perform(get("/accounts/" + myAccountNickname))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("targetAccount", "account", "isFollowing", "isOwner", "friendshipData"))
                .andExpect(view().name("accounts/profile"));
    }
}