package com.brandpark.sharemusic.testUtils;


import com.brandpark.sharemusic.modules.account.account.domain.Account;
import com.brandpark.sharemusic.modules.account.account.domain.AccountRepository;
import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.modules.account.account.form.SignUpForm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@ActiveProfiles("test")
@Component
public class AccountFactory {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private String password = "000000000";

    public List<Account> persistAccountList(String name, int size) {
        return accountRepository.saveAll(createAccountList(name, size));
    }

    public Account persistAccount(String name) {
        return accountRepository.save(createAccount(name));
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
        form.setPassword(password);
        form.setConfirmPassword(password);

        return form;
    }

    public List<Account> createAccountList(String name, int size) {
        List<Account> result = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            result.add(createAccount(name + i));
        }

        return result;
    }

    public Account persistAccount(String name, Role role) {
        return accountRepository.save(createAccount(name, role));
    }
}
