package com.brandpark.sharemusic.modules.account.dto;

import com.brandpark.sharemusic.modules.account.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@Data
public class CreateAccountDto {
    private String email;
    private String name;
    private String nickname;
    private String password;
    private String confirmPassword;

    public Account toEntity(PasswordEncoder encoder) {
        return Account.createAccount(
                email,
                name,
                nickname,
                encoder.encode(password)
        );
    }
}
