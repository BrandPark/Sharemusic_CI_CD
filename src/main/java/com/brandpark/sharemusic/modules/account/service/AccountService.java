package com.brandpark.sharemusic.modules.account.service;

import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.auth.CustomUserDetails;
import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.account.domain.Role;
import com.brandpark.sharemusic.modules.account.dto.CreateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdateAccountDto;
import com.brandpark.sharemusic.modules.account.dto.UpdatePasswordDto;
import com.brandpark.sharemusic.modules.event.FollowEvent;
import com.brandpark.sharemusic.modules.account.domain.Follow;
import com.brandpark.sharemusic.modules.account.domain.FollowRepository;
import com.brandpark.sharemusic.modules.util.MyUtil;
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

        SessionAccount newAccount = new SessionAccount(createAccount(data));

        login(newAccount);

        return newAccount;
    }

    @Transactional
    public void updateBasicInfo(UpdateAccountDto data, SessionAccount account) {
        data.setBio(MyUtil.toBrTag(data.getBio()));

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

        login(new SessionAccount(verifiedAccount));
    }

    @Transactional
    public Account createAccount(CreateAccountDto data) {
        Account newAccount = Account.createAccount(
                data.getEmail(),
                data.getName(),
                data.getNickname(),
                passwordEncoder.encode(data.getPassword()));

        return accountRepository.save(newAccount);
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

        String encodedUpdatePassword = passwordEncoder.encode(data.getUpdatePassword());

        myAccount.updatePassword(encodedUpdatePassword);
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

        Follow follow = followRepository.findByFollowerIdAndTargetId(followerId, targetAccountId)
                .orElseThrow(() -> new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION));

        followRepository.delete(follow);

        return follow.getId();
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {

        Account account = accountRepository.findByEmailOrNickname(emailOrNickname)
                .orElseThrow(() -> new UsernameNotFoundException(emailOrNickname));

        SessionAccount sessionAccount = new SessionAccount(account);

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
}
