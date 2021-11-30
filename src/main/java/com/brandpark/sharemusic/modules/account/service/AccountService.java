package com.brandpark.sharemusic.modules.account.service;

import com.brandpark.sharemusic.infra.config.auth.CustomUserDetails;
import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.infra.config.session.dto.AccountDto;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.follow.domain.Follow;
import com.brandpark.sharemusic.modules.follow.domain.FollowRepository;
import com.brandpark.sharemusic.modules.account.dto.CreateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdatePasswordDto;
import com.brandpark.sharemusic.modules.event.FollowEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final FollowRepository followRepository;

    @Transactional
    public SessionAccount signUp(CreateAccountDto data) {

        Account newAccount = createAccount(data);

        SessionAccount newSessionAccount = createSessionAccount(newAccount);

        login(newSessionAccount);

        return newSessionAccount;
    }

    @Transactional
    public void updateBasicInfo(UpdateAccountDto data, SessionAccount account) {
        updateAccountInfo(data, account.getId());

        login(account);
    }

    @Transactional
    public void updatePasswordInfo(UpdatePasswordDto data, SessionAccount account) {
        updateAccountPassword(data, account.getId());

        login(account);
    }

    @Transactional
    public void succeedVerifyEmailCheckToken(SessionAccount account) {
        Account verifiedAccount = assignUserRole(account.getId());

        login(createSessionAccount(verifiedAccount));
    }

    @Transactional
    public Account createAccount(CreateAccountDto data) {
        return accountRepository.save(data.toEntity(passwordEncoder));
    }

    @Transactional
    public void updateAccountInfo(UpdateAccountDto data, Long targetAccountId) {

        Account targetAccount = accountRepository.findById(targetAccountId).get();

        targetAccount.updateInfo(
                data.getName(),
                data.getNickname(),
                data.getBio(),
                data.getProfileImage()
        );
    }

    @Transactional
    public void updateAccountPassword(UpdatePasswordDto data, Long targetAccountId) {
        Account myAccount = accountRepository.findById(targetAccountId).get();

        myAccount.updatePassword(passwordEncoder.encode(data.getUpdatePassword()));
    }

    @Transactional
    public Account assignUserRole(Long accountId) {
        Account account = accountRepository.findById(accountId).get();
        account.assignRole(Role.USER);

        return account;
    }

    @Transactional
    public Long doFollow(Long followerId, Long targetAccountId) {

        final Account follower = accountRepository.findById(followerId).get();
        final Account target = accountRepository.findById(targetAccountId).get();

        eventPublisher.publishEvent(FollowEvent.builder()
                .follower(follower)
                .followingTarget(target)
                .build());

        return followRepository.save(Follow.builder()
                .follower(follower)
                .target(target)
                .build()).getId();
    }

    @Transactional
    public Long doUnfollow(Long followerId, Long targetAccountId) {

        Follow follow = followRepository.findByFollowerIdAndTargetId(followerId, targetAccountId).get();

        followRepository.delete(follow);

        return follow.getId();
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmailOrNickname(emailOrNickname)
                .orElseThrow(() -> new UsernameNotFoundException(emailOrNickname));

        SessionAccount sessionAccount = createSessionAccount(account);

        return new CustomUserDetails(sessionAccount);
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

    private SessionAccount createSessionAccount(Account account) {
        SessionAccount newAccount = new SessionAccount(new AccountDto(
                account.getId(),
                account.getName(),
                account.getNickname(),
                account.getEmail(),
                account.getPassword(),
                account.getBio(),
                account.getProfileImage(),
                account.getRole(),
                account.getEmailCheckToken()
        ));

        return newAccount;
    }
}
