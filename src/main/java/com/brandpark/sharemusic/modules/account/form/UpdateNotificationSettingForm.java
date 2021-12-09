package com.brandpark.sharemusic.modules.account.form;

import com.brandpark.sharemusic.infra.config.session.SessionAccount;
import com.brandpark.sharemusic.modules.account.dto.UpdateNotificationSettingDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateNotificationSettingForm {

    private boolean notificationAlbumCreatedByMyFollowing;

    private boolean notificationCommentOnMyAlbum;

    private boolean notificationFollowMe;

    public UpdateNotificationSettingForm(SessionAccount account) {
        notificationFollowMe = account.isNotificationFollowMe();
        notificationCommentOnMyAlbum = account.isNotificationCommentOnMyAlbum();
        notificationAlbumCreatedByMyFollowing = account.isNotificationAlbumCreatedByMyFollowing();
    }

    public UpdateNotificationSettingDto toModuleDto() {
        return new UpdateNotificationSettingDto(
                notificationAlbumCreatedByMyFollowing,
                notificationCommentOnMyAlbum,
                notificationFollowMe
        );
    }
}
