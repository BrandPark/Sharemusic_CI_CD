package com.brandpark.sharemusic.modules.account.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateNotificationSettingDto {
    private boolean notificationAlbumCreatedByMyFollowing;

    private boolean notificationCommentOnMyAlbum;

    private boolean notificationFollowMe;
}
