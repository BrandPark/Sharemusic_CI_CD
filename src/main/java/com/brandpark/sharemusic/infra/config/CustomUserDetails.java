package com.brandpark.sharemusic.infra.config;

import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Getter
public class CustomUserDetails extends User {

    private SessionAccount sessionAccount;

    public CustomUserDetails(SessionAccount sessionAccount) {
        super(sessionAccount.getNickname()
                , sessionAccount.getPassword()
                , Collections.singleton(new SimpleGrantedAuthority(sessionAccount.getRole().getKey())));

        this.sessionAccount = sessionAccount;
    }
}
