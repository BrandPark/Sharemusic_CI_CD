package com.brandpark.sharemusic.modules.account.service;

import com.brandpark.sharemusic.infra.config.CustomUserDetails;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.MyUtil;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
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

        login(mapToSessionAccount(newAccount));
        return newAccount;
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmailOrNickname(emailOrNickname)
                .orElseThrow(() -> new UsernameNotFoundException(emailOrNickname));

        SessionAccount sessionAccount = mapToSessionAccount(account);

        return new CustomUserDetails(sessionAccount);
    }

    @Transactional
    public void updateBasicInfo(UpdateBasicInfoForm form, SessionAccount account) {

        form.setEmail(account.getEmail());
        form.setBio(MyUtil.toBrTag(form.getBio()));

        Account persistentAccount  = accountRepository.findByEmail(form.getEmail());

        fieldMapping(form, persistentAccount);

        login(account);
    }

    @Transactional
    public void updatePassword(UpdatePasswordForm form, SessionAccount account) {

        UpdatePasswordForm encodedForm = encodingPassword(form);

        Account persistentAccount = accountRepository.findById(account.getId()).get();
        fieldMapping(encodedForm, persistentAccount);

        login(mapToSessionAccount(persistentAccount));
    }

    @Transactional
    public void succeedVerifyEmailCheckToken(SessionAccount account) {
        Account persistAccount = accountRepository.findByEmail(account.getEmail());
        persistAccount.assignRole(Role.USER);

        login(mapToSessionAccount(persistAccount));
    }

    public SessionAccount mapToSessionAccount(Account account) {
        return modelMapper.map(account, SessionAccount.class);
    }

    private Account fieldMapping(UpdateBasicInfoForm form, Account account) {
        modelMapper.map(form, account);
        return account;
    }

    private Account fieldMapping(UpdatePasswordForm form, Account account) {
        modelMapper.map(form, account);
        return account;
    }

    public UpdateBasicInfoForm mapToForm(SessionAccount account) {
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

    private void login(SessionAccount account) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new CustomUserDetails(account)
                , account.getPassword()
                , Collections.singleton(new SimpleGrantedAuthority(account.getRole().getKey()))
        );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authenticationToken);
    }
}