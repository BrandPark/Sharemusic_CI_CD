package com.brandpark.sharemusic.modules.account.service;

import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.CustomUserDetails;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.form.SignUpForm;
import com.brandpark.sharemusic.modules.account.form.UpdateBasicInfoForm;
import com.brandpark.sharemusic.modules.account.form.UpdatePasswordForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account signUp(SignUpForm form) {

        SignUpForm encodedForm = encodingPassword(form);

        Account newAccount = createAccount(encodedForm);

        accountRepository.save(newAccount);

        login(newAccount);
        return newAccount;
    }

    public void login(Account newAccount) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(newAccount)
                , newAccount.getPassword()
                , Collections.singleton(new SimpleGrantedAuthority(newAccount.getRole().getKey()))
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authenticationToken);
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmailOrNickname(emailOrNickname)
                .orElseThrow(() -> new UsernameNotFoundException(emailOrNickname));

        return new CustomUserDetails(account);
    }

    @Transactional
    public void updateBasicInfo(UpdateBasicInfoForm form, Account account) {
        Account persistentAccount  = accountRepository.findByEmail(form.getEmail());

        fieldMapping(form, persistentAccount);

        login(account);
    }

    @Transactional
    public void updatePassword(UpdatePasswordForm form, Account account) {

        Account persistentAccount = accountRepository.findById(account.getId()).get();

        UpdatePasswordForm encodedForm = encodingPassword(form);

        fieldMapping(encodedForm, persistentAccount);

        login(persistentAccount);
    }

    public Account fieldMapping(UpdateBasicInfoForm form, Account account) {
        modelMapper.map(form, account);
        return account;
    }

    public Account fieldMapping(UpdatePasswordForm form, Account account) {
        modelMapper.map(form, account);
        return account;
    }

    public UpdateBasicInfoForm entityToForm(Account account) {
        return modelMapper.map(account, UpdateBasicInfoForm.class);
    }

    private Account createAccount(SignUpForm form) {

        Account newAccount = form.toEntity();

        newAccount.generateEmailCheckToken();
        newAccount.assignRole(Role.GUEST);

        return newAccount;
    }

    private UpdatePasswordForm encodingPassword(UpdatePasswordForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);

        return form;
    }

    private SignUpForm encodingPassword(SignUpForm form) {
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        return form;
    }
}
