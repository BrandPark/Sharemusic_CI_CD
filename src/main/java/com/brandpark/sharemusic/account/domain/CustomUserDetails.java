package com.brandpark.sharemusic.account.domain;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Getter
public class CustomUserDetails extends User {

    private Account account;

    public CustomUserDetails(Account account) {
        super(account.getNickname()
                , account.getPassword()
                , Collections.singleton(new SimpleGrantedAuthority(account.getRole().getKey())));

        this.account = account;
    }
}
