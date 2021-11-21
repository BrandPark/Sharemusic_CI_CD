package com.brandpark.sharemusic.api.v1.account.dto;

import com.brandpark.sharemusic.modules.account.dto.UpdateAccountDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UpdateAccountRequest {

    @Pattern(regexp = "^[a-zA-Zㄱ-ㅎ가-힣]+$", message = "영문, 한글만 가능합니다.")
    @NotBlank(message = "이름을 입력해 주세요.")
    private String name;

    @Pattern(regexp = "^[0-9a-zA-Zㄱ-ㅎ가-힣]+$", message = "영문, 한글, 숫자만 가능합니다.")
    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String nickname;

    private String bio;

    private String profileImage;

    public UpdateAccountDto toModuleAccount() {
        return new UpdateAccountDto(
                name,
                nickname,
                bio,
                profileImage
        );
    }
}
