package com.brandpark.sharemusic.modules.account;

import com.brandpark.sharemusic.MockMvcTest;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.form.UpdateBasicInfoForm;
import com.brandpark.sharemusic.modules.account.form.UpdatePasswordForm;
import com.brandpark.sharemusic.testUtils.AccountFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired AccountFactory accountFactory;
    @Autowired PasswordEncoder passwordEncoder;
    Account savedAccount;
    Account otherSavedAccount;

    @BeforeEach
    public void setUp() {
        savedAccount = accountFactory.createAccount("savedAccount");
        accountRepository.save(savedAccount);

        otherSavedAccount = accountFactory.createAccount("otherSavedAccount");
        accountRepository.save(otherSavedAccount);
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
        String updateNickname = otherSavedAccount.getNickname();

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
                        .param("originPassword", wrongCurrentPassword)
                        .param("updatePassword", "newPassword")
                        .param("confirmPassword", "newPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("updatePasswordForm", "originPassword", "error.originPassword"))
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
                        .param("originPassword", "000000000")
                        .param("updatePassword", newPassword)
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
        updatePasswordForm.setOriginPassword("000000000");
        updatePasswordForm.setUpdatePassword("newPassword");
        updatePasswordForm.setConfirmPassword("newPassword");

        // when
        mockMvc.perform(post("/accounts/edit/password")
                        .param("originPassword", updatePasswordForm.getOriginPassword())
                        .param("updatePassword", updatePasswordForm.getUpdatePassword())
                        .param("confirmPassword", updatePasswordForm.getConfirmPassword())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("updateMessage"))
                .andExpect(view().name("redirect:/accounts/edit/password"));

        // then
        Account account = accountRepository.findByEmail(savedAccount.getEmail());
        assertTrue(passwordEncoder.matches(updatePasswordForm.getUpdatePassword(), account.getPassword()));
    }
}