package com.brandpark.sharemusic.modules;


import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.dto.SignUpForm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class AccountFactory {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public Account createAccount(String name) {
        SignUpForm signUpForm = createSignUpForm(name);

        return createAccount(signUpForm);
    }

    public Account createAccount(String name, Role role) {
        SignUpForm signUpForm = createSignUpForm(name);

        return createAccount(signUpForm, role);
    }

    public Account createAccount(SignUpForm form) {
        form.setPassword(passwordEncoder.encode(form.getPassword()));

        Account newAccount = modelMapper.map(form, Account.class);
        newAccount.generateEmailCheckToken();
        newAccount.assignRole(Role.GUEST);

        return newAccount;
    }

    public Account createAccount(SignUpForm form, Role role) {
        Account newAccount = createAccount(form);

        newAccount.assignRole(role);

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
}
