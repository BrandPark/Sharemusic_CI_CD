package com.brandpark.sharemusic.account;

import com.brandpark.sharemusic.account.domain.Account;
import com.brandpark.sharemusic.account.domain.AccountRepository;
import com.brandpark.sharemusic.account.domain.CustomUserDetails;
import com.brandpark.sharemusic.account.domain.Role;
import com.brandpark.sharemusic.account.dto.SignUpForm;
import com.brandpark.sharemusic.account.dto.UpdateBasicInfoForm;
import com.brandpark.sharemusic.infra.mail.MailMessage;
import com.brandpark.sharemusic.infra.mail.MailService;
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
    private final MailService mailService;

    @Transactional
    public Account createAccount(SignUpForm form) {

        form.setPassword(passwordEncoder.encode(form.getPassword()));

        Account newAccount = modelMapper.map(form, Account.class);
        newAccount.generateEmailCheckToken();
        newAccount.assignRole(Role.GUEST);

        return accountRepository.save(newAccount);
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

    public void sendConfirmMail(Account account) {

        MailMessage message = new MailMessage();
        message.setText("/accounts/check-email-token?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail());
        message.setTitle("ShareMusic");
        message.setTo(account.getEmail());

        mailService.send(message);
    }

    @Transactional
    public void updateBasicInfo(UpdateBasicInfoForm form, Account account) {
        Account persistAccount = accountRepository.findById(account.getId()).get();
        modelMapper.map(form, account);
    }
}
