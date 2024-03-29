package com.brandpark.sharemusic.modules.account.account.form;

import com.brandpark.sharemusic.modules.account.account.dto.UpdatePasswordDto;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class UpdatePasswordForm {

    @NotBlank(message = "현재 비밀번호를 입력해 주세요.")
    @Length(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해 주세요.")
    private String originPassword;

    @NotBlank(message = "새 비밀번호를 입력해 주세요.")
    @Length(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해 주세요.")
    private String updatePassword;

    @NotBlank(message = "새 비밀번호를 다시 한번 입력해 주세요.")
    @Length(min = 8, max = 30, message = "비밀번호는 8자 이상 30자 이하로 입력해 주세요.")
    private String confirmPassword;

    public UpdatePasswordDto toModuleDto() {
        return new UpdatePasswordDto(
                originPassword,
                updatePassword,
                confirmPassword
        );
    }
}
