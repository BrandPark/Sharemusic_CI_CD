package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import com.brandpark.sharemusic.account.dto.UpdateBasicInfoForm;
import com.brandpark.sharemusic.account.dto.UpdatePasswordForm;
import com.brandpark.sharemusic.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;
    Account savedAccount;
    Account savedAccount2;

    @BeforeEach
    public void setUp() {
        SignUpForm form = new SignUpForm();
        form.setEmail("savedAccount@email.com");
        form.setName("savedAccount");
        form.setNickname("savedAccount");
        form.setPassword("000000000");
        form.setConfirmPassword("000000000");
        savedAccount = accountService.signUp(form);

        SignUpForm form2 = new SignUpForm();
        form2.setEmail("savedAccount2@email.com");
        form2.setName("savedAccount2");
        form2.setNickname("savedAccount2");
        form2.setPassword("000000000");
        form2.setConfirmPassword("000000000");
        savedAccount2 = accountService.signUp(form2);
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("기본 정보 변경 화면 출력")
    @Test
    public void UpdateBasicInfoForm() throws Exception {

        // given, when, then
        mockMvc.perform(get("/accounts/edit/basicinfo"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account", "updateBasicInfoForm"))
                .andExpect(view().name("accounts/settings/basic-info"));
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("기본 정보 변경 처리 - 입력 값 오류(이미 사용중인 닉네임 입력)")
    @Test
    public void UpdateBasicInfo_Fail_When_InputDuplicatedNickname() throws Exception {
    
        // given
        String updateNickname = savedAccount2.getNickname();
                
        // when, then
        mockMvc.perform(post("/accounts/edit/basicinfo")
                        .param("name", savedAccount.getName())
                        .param("nickname", updateNickname)
                        .param("bio", savedAccount.getBio())
                        .param("profileImage", savedAccount.getProfileImage())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account", "updateBasicInfoForm"))
                .andExpect(model().attributeHasFieldErrorCode("updateBasicInfoForm", "nickname"
                        , "error.nickname"))
                .andExpect(view().name("accounts/settings/basic-info"));
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("기본 정보 변경 처리 - 입력 값 무시(이메일 값 변경))")
    @Test
    public void UpdateBasicInfo_Ignore_When_InputModifiedEmail() throws Exception {

        // given
        String modifiedEmail = "modified@email.com";

        // when
        mockMvc.perform(post("/accounts/edit/basicinfo")
                        .param("email", modifiedEmail)
                        .param("name", savedAccount.getName())
                        .param("nickname", savedAccount.getNickname())
                        .param("bio", savedAccount.getBio())
                        .param("profileImage", savedAccount.getProfileImage())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("updateMessage"))
                .andExpect(view().name("redirect:/accounts/edit/basicinfo"));

        // then
        assertThat(savedAccount.getEmail()).isNotEqualTo(modifiedEmail);
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("기본 정보 변경 처리 - 성공")
    @Test
    public void UpdateBasicInfo_Success() throws Exception {

        // given
        UpdateBasicInfoForm updateForm = new UpdateBasicInfoForm();
        updateForm.setName("modifiedName");
        updateForm.setNickname("modifiedNickname");
        updateForm.setProfileImage("modifiedImage");
        updateForm.setBio("modifiedBio");

        // when
        mockMvc.perform(post("/accounts/edit/basicinfo")
                        .param("name", updateForm.getName())
                        .param("nickname", updateForm.getNickname())
                        .param("bio", updateForm.getBio())
                        .param("profileImage", updateForm.getProfileImage())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("updateMessage"))
                .andExpect(view().name("redirect:/accounts/edit/basicinfo"));

        // then
        Account updatedAccount = accountRepository.findByNickname(updateForm.getNickname());
        assertThat(updatedAccount.getName()).isEqualTo(updateForm.getName());
        assertThat(updatedAccount.getNickname()).isEqualTo(updateForm.getNickname());
        assertThat(updatedAccount.getBio()).isEqualTo(updateForm.getBio());
        assertThat(updatedAccount.getProfileImage()).isEqualTo(updateForm.getProfileImage());
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 화면 출력")
    @Test
    public void UpdatePasswordForm() throws Exception {

        // given, when, then
        mockMvc.perform(get("/accounts/edit/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account", "updatePasswordForm"))
                .andExpect(view().name("accounts/settings/password"));
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 처리 - 입력 값 오류(현재 비밀번호 오류)")
    @Test
    public void UpdatePassword_Fail_When_InputWrongCurrentPassword() throws Exception {

        // given
        String wrongCurrentPassword = "wrongCurrentPassword";

        // when, then
        mockMvc.perform(post("/accounts/edit/password")
                        .param("currentPassword", wrongCurrentPassword)
                        .param("password", "newPassword")
                        .param("confirmPassword", "newPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("updatePasswordForm", "currentPassword", "error.currentPassword"))
                .andExpect(model().attributeExists("account", "updatePasswordForm"))
                .andExpect(view().name("accounts/settings/password"));
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 처리 - 입력 값 오류(새 비밀번호 확인 오류)")
    @Test
    public void UpdatePassword_Fail_When_InputDifferentConfirmPassword() throws Exception {

        // given
        String newPassword = "newPassword";

        // when, then
        mockMvc.perform(post("/accounts/edit/password")
                        .param("currentPassword", "000000000")
                        .param("password", newPassword)
                        .param("confirmPassword", newPassword + "diff")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("updatePasswordForm", "confirmPassword", "error.confirmPassword"))
                .andExpect(model().attributeExists("account", "updatePasswordForm"))
                .andExpect(view().name("accounts/settings/password"));
    }

    @WithUserDetails(value = "savedAccount", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("비밀번호 변경 처리 - 성공")
    @Test
    public void UpdatePassword_Success() throws Exception {

        // given
        UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm();
        updatePasswordForm.setCurrentPassword("000000000");
        updatePasswordForm.setPassword("newPassword");
        updatePasswordForm.setConfirmPassword("newPassword");

        // when
        mockMvc.perform(post("/accounts/edit/password")
                        .param("currentPassword", updatePasswordForm.getCurrentPassword())
                        .param("password", updatePasswordForm.getPassword())
                        .param("confirmPassword", updatePasswordForm.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("updateMessage"))
                .andExpect(view().name("redirect:/accounts/edit/password"));

        // then
        Account account = accountRepository.findByEmail(savedAccount.getEmail());
        assertTrue(passwordEncoder.matches(updatePasswordForm.getPassword(), account.getPassword()));
    }
}