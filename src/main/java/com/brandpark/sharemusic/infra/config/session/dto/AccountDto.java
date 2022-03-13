package com.brandpark.sharemusic.infra.config.session.dto;

import com.brandpark.sharemusic.infra.config.auth.Role;
import com.brandpark.sharemusic.modules.account.account.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountDto {

    private Long id;

    private String name;

    private String nickname;

    private String email;

    private String password;

    private String bio;

    private String profileImage;

    private Role role;

    private String emailCheckToken;

    private boolean notificationAlbumCreatedByMyFollowing;

    private boolean notificationCommentOnMyAlbum;

    private boolean notificationFollowMe;

    public static AccountDto of(Account account) {
        AccountDto ret = new AccountDto();

        ret.id = account.getId();
        ret.name = account.getName();
        ret.nickname = account.getNickname();
        ret.email = account.getEmail();
        ret.password = account.getPassword();
        ret.bio = account.getBio();
        ret.profileImage = account.getProfileImage();
        ret.role = account.getRole();
        ret.emailCheckToken = account.getEmailCheckToken();
        ret.notificationAlbumCreatedByMyFollowing = account.isNotificationAlbumCreatedByMyFollowing();
        ret.notificationCommentOnMyAlbum = account.isNotificationCommentOnMyAlbum();
        ret.notificationFollowMe = account.isNotificationFollowMe();

        return ret;
    }
}
