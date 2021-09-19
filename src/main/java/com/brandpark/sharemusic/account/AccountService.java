package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.Role;
import com.brandpark.sharemusic.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
    public Account createAccount(SignUpForm form) {

        form.setPassword(passwordEncoder.encode(form.getPassword()));
        Account newAccount = createAccountImpl(form);

        return accountRepository.save(newAccount);
    }

    private Account createAccountImpl(SignUpForm form) {

        Account newAccount = modelMapper.map(form, Account.class);
        newAccount.generateEmailCheckToken();
        newAccount.assignRole(Role.GUEST);

        return newAccount;
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

        return new User(
                emailOrNickname
                , account.getPassword()
                , Collections.singleton(new SimpleGrantedAuthority(account.getRole().getKey()))
        );
    }
}
