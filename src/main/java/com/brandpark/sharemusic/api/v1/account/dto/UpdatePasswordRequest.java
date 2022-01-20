package com.brandpark.sharemusic.api.v1.account.dto;

import com.brandpark.sharemusic.modules.account.account.dto.UpdatePasswordDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String originPassword;

    @NotBlank(message = "변경 할 비밀번호를 입력해주세요.")
    private String updatePassword;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String confirmPassword;

    public UpdatePasswordDto toModuleDto() {
        return new UpdatePasswordDto(
                originPassword,
                updatePassword,
                confirmPassword
        );
    }
}


