package com.brandpark.sharemusic.infra.config.session;

import com.brandpark.sharemusic.infra.config.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SessionAccount implements Serializable {

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

    /*public SessionAccount(AccountDto accountDto) {
        id = accountDto.getId();
        name = accountDto.getName();
        nickname = accountDto.getNickname();
        email = accountDto.getEmail();
        password = accountDto.getPassword();
        bio = accountDto.getBio();
        profileImage = accountDto.getProfileImage();
        role = accountDto.getRole();
        emailCheckToken = accountDto.getEmailCheckToken();
        notificationAlbumCreatedByMyFollowing = accountDto.isNotificationAlbumCreatedByMyFollowing();
        notificationFollowMe = accountDto.isNotificationFollowMe();
        notificationCommentOnMyAlbum = accountDto.isNotificationCommentOnMyAlbum();
    }*/
}
