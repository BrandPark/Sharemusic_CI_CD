package com.brandpark.sharemusic.account.domain;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class UserAccount extends User {

    public UserAccount(Account account) {
        super(account.getEmail(),
                account.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(account.getRole().getKey())));
    }
}
