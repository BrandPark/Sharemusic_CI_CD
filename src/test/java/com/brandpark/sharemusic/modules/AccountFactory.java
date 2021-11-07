package com.brandpark.sharemusic.modules;


import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.form.SignUpForm;
import com.brandpark.sharemusic.modules.account.form.UpdateBasicInfoForm;
import com.brandpark.sharemusic.modules.account.form.UpdatePasswordForm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Component
public class AccountFactory {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public SessionAccount createSessionAccount(String name) {
        Account account = createAccount(name);
        return modelMapper.map(account, SessionAccount.class);
    }

    public Account createAccount(String name) {
        SignUpForm signUpForm = createSignUpForm(name);

        return createAccount(signUpForm);
    }

    public Account createAccount(String name, Role role) {
        SignUpForm signUpForm = createSignUpForm(name);

        return createAccount(signUpForm, role);
    }

    public Account createAccount(SignUpForm form, Role role) {
        Account newAccount = createAccount(form);

        newAccount.assignRole(role);

        return newAccount;
    }

    public Account createAccount(SignUpForm form) {
        form.setPassword(passwordEncoder.encode(form.getPassword()));

        Account newAccount = Account.builder()
                .name(form.getName())
                .nickname(form.getNickname())
                .password(form.getPassword())
                .bio("My name is " + form.getName())
                .email(form.getEmail())
                .profileImage("image")
                .build();

        newAccount.generateEmailCheckToken();
        newAccount.assignRole(Role.GUEST);

        return newAccount;
    }

    public SignUpForm createSignUpForm(String name) {
        SignUpForm form = new SignUpForm();
        form.setEmail(name + "@email.com");
        form.setName(name);
        form.setNickname(name);
        form.setPassword("000000000");
        form.setConfirmPassword("000000000");

        return form;
    }

    public UpdateBasicInfoForm createUpdateBasicInfoForm(String name) {
        UpdateBasicInfoForm form = new UpdateBasicInfoForm();
        form.setName(name);
        form.setEmail(name + "@email.com");
        form.setNickname(name);
        form.setBio("My name is" + name);
        form.setProfileImage("image");

        return form;
    }

    public UpdatePasswordForm createUpdatePasswordForm() {
        UpdatePasswordForm form = new UpdatePasswordForm();
        form.setCurrentPassword("000000000");
        form.setPassword("111111111");
        form.setConfirmPassword("111111111");

        return form;
    }

    public List<Account> createAccountList(String name, int size) {
        List<Account> result = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            result.add(createAccount(name + i));
        }

        return result;
    }
}
