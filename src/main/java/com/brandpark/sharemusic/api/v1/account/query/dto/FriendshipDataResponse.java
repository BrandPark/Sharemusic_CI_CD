package com.brandpark.sharemusic.api.v1.account.query.dto;

import lombok.Data;

@Data
public class FriendshipDataResponse {
    private long followerCount;
    private long followingCount;
    private long albumCount;
}
