package com.brandpark.sharemusic.modules.account.service;

import com.brandpark.sharemusic.modules.AccountFactory;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.form.SignUpForm;
import com.brandpark.sharemusic.modules.account.form.UpdateBasicInfoForm;
import com.brandpark.sharemusic.modules.account.form.UpdatePasswordForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class AccountServiceTest {

    @Autowired AccountService accountService;
    @Autowired AccountFactory accountFactory;
    @Autowired PasswordEncoder passwordEncoder;

    @DisplayName("회원가입 시 비밀번호 인코딩")
    @Test
    public void PasswordEncoding_When_SignUp() throws Exception {

        // given
        accountService = (AccountService) AopProxyUtils.getSingletonTarget(this.accountService);
        Method method = accountService.getClass().getDeclaredMethod("encodingPassword", SignUpForm.class);
        method.setAccessible(true);

        SignUpForm form = accountFactory.createSignUpForm("newAccount");
        String rawPassword = form.getPassword();

        // when
        SignUpForm encodedForm = (SignUpForm) method.invoke(accountService, form);

        // then
        assertThat(passwordEncoder.matches(rawPassword, encodedForm.getPassword())).isTrue();
    }

    @DisplayName("비밀번호 변경 시 비밀번호 인코딩")
    @Test
    public void PasswordEncoding_When_UpdatePassword() throws Exception {

        // given
        accountService = (AccountService) AopProxyUtils.getSingletonTarget(this.accountService);
        Method method = accountService.getClass().getDeclaredMethod("encodingPassword", UpdatePasswordForm.class);
        method.setAccessible(true);

        UpdatePasswordForm form = accountFactory.createUpdatePasswordForm();
        String rawPassword = form.getPassword();

        // when
        UpdatePasswordForm encodedForm = (UpdatePasswordForm) method.invoke(accountService, form);

        // then
        assertThat(passwordEncoder.matches(rawPassword, encodedForm.getPassword())).isTrue();
    }

    @DisplayName("Account -> UpdateBasicInfoForm 필드 단순 삽입")
    @Test
    public void Account_To_UpdateBasicInfoForm() throws Exception {

        // given
        Account account = accountFactory.createAccount("newAccount");

        // when
        UpdateBasicInfoForm form = accountService.entityToForm(account);

        // then
        assertThat(form.getEmail()).isEqualTo(account.getEmail());
        assertThat(form.getNickname()).isEqualTo(account.getNickname());
        assertThat(form.getBio()).isEqualTo(account.getBio());
        assertThat(form.getName()).isEqualTo(account.getName());
        assertThat(form.getProfileImage()).isEqualTo(account.getProfileImage());
    }

    @DisplayName("UpdateBasicInfoForm -> Account 필드 단순 삽입")
    @Test
    public void UpdateBasicInfoForm_SimpleInsertTo_Account() throws Exception {

        // given
        Account account = accountFactory.createAccount("newAccount");
        UpdateBasicInfoForm form = accountFactory.createUpdateBasicInfoForm("updateAccount");

        // when
        accountService.fieldMapping(form, account);

        // then
        assertThat(account.getEmail()).isEqualTo(form.getEmail());
        assertThat(account.getNickname()).isEqualTo(form.getNickname());
        assertThat(account.getName()).isEqualTo(form.getName());
        assertThat(account.getBio()).isEqualTo(form.getBio());
        assertThat(account.getProfileImage()).isEqualTo(form.getProfileImage());
    }

    @DisplayName("UpdatePasswordForm -> Account 필드 단순 삽입")
    @Test
    public void UpdatePasswordForm_SimpleInsertTo_Account() throws Exception {

        // given
        Account account = accountFactory.createAccount("newAccount");
        UpdatePasswordForm form = accountFactory.createUpdatePasswordForm();

        // when
        accountService.fieldMapping(form, account);

        // then
        assertThat(account.getPassword()).isEqualTo(form.getPassword());
    }
}