package com.brandpark.sharemusic.modules.account.form;

import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.dto.UpdateAccountDto;
import com.brandpark.sharemusic.modules.util.MyUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Data
public class UpdateBasicInfoForm {

    private String email;

    @Pattern(regexp = "^[a-zA-Zㄱ-ㅎ가-힣]+$", message = "영문, 한글만 가능합니다.")
    @NotBlank(message = "이름을 입력해 주세요.")
    @Length(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해 주세요.")
    private String name;

    @Pattern(regexp = "^[0-9a-zA-Zㄱ-ㅎ가-힣_-]+$", message = "영문, 한글, 숫자, 특수문자(_, -)만 가능합니다.")
    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Length(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해 주세요.")
    private String nickname;

    @Length(max = 100, message = "소개는 100자 이하로 작성해주시기 바랍니다.")
    private String bio;

    private String profileImage;

    public UpdateBasicInfoForm(SessionAccount account) {
        email = account.getEmail();
        name = account.getName();
        nickname = account.getNickname();
        bio = account.getBio();
        profileImage = account.getProfileImage();
    }

    public UpdateAccountDto toModuleDto() {
        return new UpdateAccountDto(
                name,
                nickname,
                MyUtil.toEscape(bio),
                profileImage
        );
    }


}
