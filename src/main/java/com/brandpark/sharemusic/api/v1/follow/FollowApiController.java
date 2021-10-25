package com.brandpark.sharemusic.api.v1.follow;

import com.brandpark.sharemusic.api.v1.exception.ApiException;
import com.brandpark.sharemusic.api.v1.exception.Error;
import com.brandpark.sharemusic.infra.config.auth.LoginAccount;
import com.brandpark.sharemusic.infra.config.dto.SessionAccount;
import com.brandpark.sharemusic.modules.account.domain.Account;
import com.brandpark.sharemusic.modules.account.domain.AccountRepository;
import com.brandpark.sharemusic.modules.follow.Follow;
import com.brandpark.sharemusic.modules.follow.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class FollowApiController {

    private final AccountRepository accountRepository;
    private final FollowRepository followRepository;

    @PostMapping("/accounts/{targetId}/follow")
    public Long doFollow(@LoginAccount SessionAccount account, @PathVariable Long targetId) {

        Account follower = accountRepository.findById(account.getId())
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        Account target = accountRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        return followRepository.save(Follow.builder()
                .follower(follower)
                .target(target)
                .build()).getId();
    }

    @PostMapping("/accounts/{targetId}/unfollow")
    public Long doUnfollow(@LoginAccount SessionAccount account, @PathVariable Long targetId) {

        Account unFollower = accountRepository.findById(account.getId())
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        Account target = accountRepository.findById(targetId)
                .orElseThrow(() -> new ApiException(Error.NOT_FOUND_ACCOUNT_EXCEPTION));

        Follow follow = followRepository.findByFollowerIdAndTargetId(unFollower.getId(), target.getId())
                .orElseThrow(() -> new ApiException(Error.ILLEGAL_ACCESS_EXCEPTION));

        followRepository.delete(follow);

        return follow.getId();
    }
}
