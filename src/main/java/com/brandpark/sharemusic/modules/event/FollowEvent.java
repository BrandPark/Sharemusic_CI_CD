package com.brandpark.sharemusic.modules.event;

import com.brandpark.sharemusic.modules.account.domain.Account;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FollowEvent {
    private Account follower;
    private Account followingTarget;
}
