package com.brandpark.sharemusic.api.v1.account.dto;

import com.brandpark.sharemusic.modules.account.account.dto.CreateAccountDto;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CreateAccountRequest {

    @Email(message = "이메일 형식이 아닙니다.", regexp = "^(.+)@(.+)$")
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;

    @Pattern(regexp = "^[a-zA-Zㄱ-ㅎ가-힣]+$", message = "영문, 한글만 가능합니다.")
    @NotBlank(message = "이름을 입력해 주세요.")
    private String name;

    @Pattern(regexp = "^[0-9a-zA-Zㄱ-ㅎ가-힣]+$", message = "영문, 한글, 숫자만 가능합니다.")
    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

    @NotBlank(message = "비밀번호를 다시 한번 입력해 주세요.")
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
