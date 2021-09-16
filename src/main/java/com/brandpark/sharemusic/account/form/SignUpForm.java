package com.brandpark.sharemusic.account.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignUpForm {

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;

    @NotBlank(message = "이름을 입력해 주세요.")
    @Length(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해 주세요.")
    private String name;

    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Length(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해 주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Length(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해 주세요.")
    private String password;

    @NotBlank(message = "비밀번호를 다시 한번 입력해 주세요.")
    @Length(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해 주세요.")
    private String confirmPassword;
}
