package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.Role;
import com.brandpark.sharemusic.account.form.SignUpForm;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
    @MockBean MailService mailService;

    @DisplayName("회원가입 화면출력")
    @Test
    public void signUpForm() throws Exception {
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
                .andExpect(view().name("accounts/signup"));
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
                .andExpect(view().name("accounts/signup"));
    }

    @DisplayName("회원가입 처리 - 입력 값 오류 (email 중복)")
    @Test
    public void SignUpSubmit_Fail_When_InputDuplicateEmail() throws Exception {
        // given
        Account savedAccount = accountService.processCreateAccount(createForm());

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
                .andExpect(view().name("accounts/signup"));
    }

    @DisplayName("회원가입 처리 - 입력 값 오류 (nickname 중복)")
    @Test
    public void SignUpSubmit_Fail_When_InputDuplicateNickname() throws Exception {
        // given
        Account savedAccount = accountService.processCreateAccount(createForm());

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
                .andExpect(view().name("accounts/signup"));
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
                .andExpect(model().hasNoErrors())
                .andExpect(view().name("redirect:/"));

        Account account = accountRepository.findByEmail("example@email.com");
        assertThat(account).isNotNull();
        assertThat(account.getEmailCheckToken()).isNotNull();
        assertThat(account.getEmailCheckTokenGeneratedAt()).isNotNull();
        assertThat(account.getRole()).isEqualTo(Role.GUEST);
        assertTrue(passwordEncoder.matches("123123123", account.getPassword()));

        then(mailService).should().send(any(MailMessage.class));
    }

    private SignUpForm createForm() {

        SignUpForm form = new SignUpForm();

        form.setName("name");
        form.setNickname("nickname");
        form.setPassword("123123123");
        form.setConfirmPassword("123123123");
        form.setEmail("example@email.com");

        return form;
    }
}