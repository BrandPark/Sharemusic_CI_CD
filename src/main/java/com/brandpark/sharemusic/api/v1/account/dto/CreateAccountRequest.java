package com.brandpark.sharemusic.api.v1.account.dto;

import com.brandpark.sharemusic.modules.account.dto.CreateAccountDto;
import lombok.Data;

@Data
public class CreateAccountRequest {
    private String email;
    private String name;
    private String nickname;
    private String password;
    private String confirmPassword;

    public CreateAccountDto toModuleDto() {
        return new CreateAccountDto(
                email,
                name,
                nickname,
                password,
                confirmPassword
        );
    }
}
