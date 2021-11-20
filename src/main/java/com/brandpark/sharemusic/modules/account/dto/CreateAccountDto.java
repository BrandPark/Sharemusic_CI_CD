package com.brandpark.sharemusic.modules.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateAccountDto {
    private String email;
    private String name;
    private String nickname;
    private String password;
    private String confirmPassword;
}
