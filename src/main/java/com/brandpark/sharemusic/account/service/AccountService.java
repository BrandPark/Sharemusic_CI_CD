package com.brandpark.sharemusic.account.service;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.CustomUserDetails;
import com.brandpark.sharemusic.account.domain.Role;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import com.brandpark.sharemusic.account.dto.UpdateBasicInfoForm;
import com.brandpark.sharemusic.account.dto.UpdatePasswordForm;
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

        Account newAccount = createAccount(form);

        login(newAccount);
        return newAccount;
    }

    public Account createAccount(SignUpForm form) {
        form.setPassword(passwordEncoder.encode(form.getPassword()));

        Account newAccount = modelMapper.map(form, Account.class);
        newAccount.generateEmailCheckToken();
        newAccount.assignRole(Role.GUEST);
        accountRepository.save(newAccount);

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

         Account account = accountRepository.findByEmail(emailOrNickname);
        if (account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if (account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new CustomUserDetails(account);
    }

    @Transactional
    public void updateBasicInfo(UpdateBasicInfoForm form, Account account) {
        account = accountRepository.findByEmail(form.getEmail());

        modelMapper.map(form, account);

        login(account);
    }

    @Transactional
    public void updatePassword(UpdatePasswordForm form, Account account) {

        Account persistAccount = accountRepository.findById(account.getId()).get();

        String encodedPassword = passwordEncoder.encode(form.getPassword());
        form.setPassword(encodedPassword);

        modelMapper.map(form, persistAccount);

        login(persistAccount);
    }
}
